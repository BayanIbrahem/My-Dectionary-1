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
import kotlinx.coroutines.flow.first

class MDRoomExportToFileRepo(
    private val fileWriterFactory: MDFileWriterFactory,
    private val wordRepo: WordRepo,
    private val typeTagRepo: TypeTagRepo,
    private val fileManager: FileManager,
) : ExportToFileRepo {
    override suspend fun export(
        wordsIds: Set<Long>,
        parts: Set<MDFilePartType>,
        exportDirectory: MDDocumentData,
        exportFileType: MDFileType,
        exportFileName: String,
    ): Result<Unit> = runCatching {
        require(exportDirectory.isDir) { "Invalid directory data $exportDirectory, (not a dir)" }
        val sfx = exportFileType.typeExtensionWithDot
        val dirFiles = fileManager.getSubDocuments(
            fileData = exportDirectory,
            includeFiles = true,
            includeDirs = false,
        ).getOrThrow().mapNotNull {
            if (it.name.lowercase().endsWith(sfx)) {
                it.name.lowercase()
            } else {
                null
            }
        }.toSet()
        var newName = exportFileName + sfx
        var i = 0
        while (newName in dirFiles) {
            newName = "${exportFileName}_${i++}$sfx"
        }
        val file = fileManager.createSubDocument(exportDirectory, newName, exportFileType.mimeType).getOrThrow()
        val writer = fileWriterFactory.getWriterForType(exportFileType)
        val outputStream = fileManager.openOutputStream(file).getOrThrow()
        writer.writeFile(
            stream = outputStream,
            parts = parts
        ) { part: MDFilePartType ->
            when (part) {
                MDFilePartType.Language -> getLanguagesOfIds(wordsIds)
                MDFilePartType.Tag -> getContextTagsOfIds(wordsIds)
                MDFilePartType.Word -> getWordsOfIds(wordsIds)
            }
        }
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