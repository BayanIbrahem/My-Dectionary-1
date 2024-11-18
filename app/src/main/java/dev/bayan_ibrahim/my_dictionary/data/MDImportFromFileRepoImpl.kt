package dev.bayan_ibrahim.my_dictionary.data

import android.util.Log
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.blankRawWord
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummaryStatus
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDImportFromFileRepo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MDImportFromFileRepoImpl(
    private val db: MDDataBase,
    private val rawWordReader: MDFileReaderDecorator<MDRawWord>,
) : MDImportFromFileRepo {
    override suspend fun checkFileIFValid(fileData: MDFileData): Boolean {
        val valid = rawWordReader.validHeader(fileData)
        return valid
    }

    override fun processFile(
        fileData: MDFileData,
        onInvalidStream: () -> Unit,
        onUnsupportedFile: () -> Unit,
        onReadStreamError: (throwable: Throwable) -> Unit,
        tryGetReaderByMimeType: Boolean,
        tryGetReaderByFileHeader: Boolean,
    ): Flow<MDFileProcessingSummary> {
        return flow {
            val words = mutableSetOf<Pair<String, String>>() // meaning to translation
            val languages = mutableSetOf<String>()
            val tags = mutableSetOf<String>()
            val wordTypeTags = mutableSetOf<String>()
            val wordTypeTagRelation = mutableSetOf<String>()
            var i = 0
            fun normalSummary() = MDFileProcessingSummary(
                wordsCount = words.count(),
                languagesCount = languages.count(),
                tagsCount = tags.count(),
                wordTypeTagCount = wordTypeTags.count(),
                wordTypeTagRelationCount = wordTypeTagRelation.count(),
                totalEntriesRead = i++,
                status = MDFileProcessingSummaryStatus.RUNNING,
            )
            fun endSummary() = MDFileProcessingSummary(
                wordsCount = words.count(),
                languagesCount = languages.count(),
                tagsCount = tags.count(),
                wordTypeTagCount = wordTypeTags.count(),
                wordTypeTagRelationCount = wordTypeTagRelation.count(),
                totalEntriesRead = i++,
                status = MDFileProcessingSummaryStatus.COMPLETED,
            )
            rawWordReader.readFile(
                fileData = fileData,
                onInvalidStream = {
                    Log.d("file_reader", "invalid input stream for file $fileData")
                    onInvalidStream()
                    emit(endSummary())
                },
                onUnsupportedFile = {
                    Log.d("file_reader", "unsupported file type $fileData")
                    onUnsupportedFile()
                    emit(endSummary())
                },
                onReadStreamError = {
                    Log.d("file_reader", "read steam error $it cause: ${it.cause} for file $fileData")
                    onReadStreamError(it)
                    emit(endSummary())
                },
                onComplete = {
                    Log.d("file_reader", "read steam ended for file $fileData")
                    emit(endSummary())
                }
            ) { word ->
                if (word != blankRawWord) {
                    Log.d("file_reader", "read file, read valid word $word")
                    words.add(word.meaning to word.translation)
                    languages.add(word.language)
                    tags.addAll(word.tags)
                    word.wordTypeTag?.let {
                        wordTypeTags.add(it.name)
                        wordTypeTagRelation.addAll(it.relations.map { it.label })
                    }
                    emit(normalSummary())
                } else {
                    Log.d("file_reader", "read file, read invalid word")
                }
                true
            }
        }.flowOn(Dispatchers.IO)
    }
}