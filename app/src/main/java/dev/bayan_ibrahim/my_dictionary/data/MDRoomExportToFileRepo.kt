package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write.MDFileWriterFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.FileManager
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part.MDJsonFileLanguagePartV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part.MDJsonFileTagPartV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part.MDJsonFileWordPartV1
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.ExportToFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TypeTagRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.toStrHex
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

private const val EXPORT_FILE_TRASH_KEY = "exportFile"

sealed interface ExportProgress {
    val isRunning: Boolean
    val isDone: Boolean

    data class Running(
        val currentFilePart: MDFilePartType,
        val index: Int,
        val partTotal: Int,
        val availableParts: Set<MDFilePartType>,
    ) : ExportProgress {
        override val isRunning = true
        override val isDone = false

        val progress get() = index.toFloat() / partTotal
        val partIndex: Int
            get() = availableParts.count { it.ordinal < currentFilePart.ordinal }
    }

    data class Done(
        val outputFile: MDDocumentData,
        val outputDir: MDDocumentData,
    ) : ExportProgress {
        override val isRunning = false
        override val isDone = true
    }

    sealed class Error(
        val message: String,
    ) : ExportProgress, LabeledEnum {
        override val isRunning = false
        override val isDone = true

        data class InvalidOutputDir(
            val exportDirectory: MDDocumentData,
        ) : Error("Invalid directory data ${exportDirectory.name}, (not a dir)")

        data class UnreadableOutputDir(
            val exportDirectory: MDDocumentData,
        ) : Error("Can not read inner files of ${exportDirectory.name}")

        data class InvalidOutputStream(
            val cause: Throwable,
        ) : Error("unable to open output file ${cause.message}")

        override val strLabel: String
            get() = when (this) {
                is InvalidOutputDir -> "Invalid Directory, try select another one"
                is UnreadableOutputDir -> "Selected Directory Can not be read"
                is InvalidOutputStream -> "Can not open file in selected directory, check if selected directory is valid and not protected"
            }// TODO, string res

        val errorName: String
            get() = when (this) {
                is InvalidOutputDir -> "Invalid Dir"
                is InvalidOutputStream -> "Protected Dir"
                is UnreadableOutputDir -> "Protected Dir"
            } // TODO, string res
    }
}

class MDRoomExportToFileRepo(
    private val fileWriterFactory: MDFileWriterFactory,
    private val wordRepo: WordRepo,
    private val typeTagRepo: TypeTagRepo,
    private val fileManager: FileManager,
) : ExportToFileRepo {
    override fun export(
        wordsIds: Set<Long>,
        parts: Set<MDFilePartType>,
        exportDirectory: MDDocumentData,
        exportFileType: MDFileType,
        exportFileName: String,
    ): Flow<ExportProgress> = flow {
        if (!exportDirectory.isDir) {
            val error = ExportProgress.Error.InvalidOutputDir(exportDirectory)
            emit(error)
            throw IllegalArgumentException(error.message)
        }
        val sfx = exportFileType.typeExtensionWithDot
        val dirFiles = fileManager.getSubDocuments(
            fileData = exportDirectory,
            includeFiles = true,
            includeDirs = false,
        ).getOrNull()?.mapNotNull {
            if (it.name.lowercase().endsWith(sfx)) {
                it.name.lowercase()
            } else {
                null
            }
        }?.toSet() ?: let {
            val error = ExportProgress.Error.UnreadableOutputDir(exportDirectory)
            emit(error)
            throw IOException(error.message)
        }
        var newName = exportFileName + sfx
        var i = 0
        while (newName in dirFiles) {
            newName = "${exportFileName}_${i++}$sfx"
        }
        val file = fileManager.createSubDocument(exportDirectory, newName, exportFileType.mimeType).getOrThrow()
        fileManager.addToTrash(EXPORT_FILE_TRASH_KEY, file)
        val writer = fileWriterFactory.getWriterForType(exportFileType)
        val outputStream = fileManager.openOutputStream(file).fold(
            onSuccess = { it },
            onFailure = {
                emit(ExportProgress.Error.InvalidOutputStream(it))
                throw it
            }
        )
        val anyWritten = writer.writeFile(
            stream = outputStream,
            parts = parts,
            onProgress = { index, total, part ->
                emit(
                    value = ExportProgress.Running(
                        currentFilePart = part,
                        index = index,
                        partTotal = total,
                        availableParts = parts
                    )
                )
            }
        ) { part: MDFilePartType ->
            when (part) {
                MDFilePartType.Language -> getLanguagesOfIds(wordsIds)
                MDFilePartType.Tag -> getContextTagsOfIds(wordsIds)
                MDFilePartType.Word -> getWordsOfIds(wordsIds)
            }
        }
        if (anyWritten) {
            fileManager.restoreFromTrash(EXPORT_FILE_TRASH_KEY)
            emit(
                value = ExportProgress.Done(
                    outputFile = file,
                    outputDir = exportDirectory
                )
            )
        } else {
            emit(
                value = ExportProgress.Done(
                    outputFile = file,
                    outputDir = exportDirectory
                )
            )
        }
    }.onCompletion {
        fileManager.clearTrash(setOf(EXPORT_FILE_TRASH_KEY))
    }

    private suspend fun getLanguagesOfIds(
        wordsIds: Set<Long>,
    ) = wordRepo.getWordsOfIds(wordsIds).first().toList().map {
        it.language
    }.distinct().map { language ->
        val typeTags = typeTagRepo.getTypeTagsOfLanguage(language).first()
        MDJsonFileLanguagePartV1(
            code = language.code,
            typeTags = typeTags.map { tag ->
                tag.asFilePartTypeTag()
            }
        )
    }


    private suspend fun getContextTagsOfIds(
        wordsIds: Set<Long>,
    ) = wordRepo.getWordsOfIds(wordsIds).first().map {
        it.tags
    }.flatten().distinctBy {
        it.id
    }.map { tag ->
        tag.asFilePartContextTag()
    }.toList()

    private suspend fun getWordsOfIds(
        wordsIds: Set<Long>,
    ) = wordRepo.getWordsOfIds(wordsIds).first().map { word ->
        MDJsonFileWordPartV1(
            language = word.language.code,
            meaning = word.meaning,
            translation = word.translation,
            transcription = word.transcription.nullIfInvalid(),
            contextTags = word.tags.map { it.asFilePartContextTag() },
            examples = word.examples,
            additionalTranslations = word.additionalTranslations,
            typeTag = word.wordTypeTag?.let { tag ->
                MDJsonFileWordPartV1.TypeTag(
                    name = tag.name
                )
            },
            relatedWordsList = word.relatedWords.map {
                MDJsonFileWordPartV1.RelatedWord(
                    name = it.relationLabel,
                    relatedWord = it.value
                )
            },
            lexicalRelatedWords = word.lexicalRelations.map { (_, values) ->
                values.map { relation ->
                    MDJsonFileWordPartV1.LexicalRelation(
                        type = relation.type,
                        value = relation.relatedWord,
                    )
                }
            }.flatten()
        )
    }.toList()

    private fun WordTypeTag.asFilePartTypeTag(): MDJsonFileLanguagePartV1.TypeTag = MDJsonFileLanguagePartV1.TypeTag(
        name = name,
        relations = relations.map { relation ->
            MDJsonFileLanguagePartV1.TypeTag.Relation(
                name = relation.label
            )
        },
    )

    private fun ContextTag.asFilePartContextTag(): MDJsonFileTagPartV1 = MDJsonFileTagPartV1(
        name = value,
        color = originalColor?.toStrHex(),
        passColorToChildren = originalColor?.let { passColorToChildren }
    )
}