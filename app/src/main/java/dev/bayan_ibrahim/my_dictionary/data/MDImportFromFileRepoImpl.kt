package dev.bayan_ibrahim.my_dictionary.data

import android.util.Log
import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWordTypeTag
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.TypeTagWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummaryStatus
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.code
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDImportFromFileRepo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private const val TAG = "file_reader"

class MDImportFromFileRepoImpl(
    private val db: MDDataBase,
    private val rawWordReader: MDFileReaderDecorator<MDRawWord>,
) : MDImportFromFileRepo {
    private val wordDao = db.getWordDao()
    private val tagDao = db.getWordTypeTagDao()
    private val languageDao = db.getLanguageDao()
    private val relationDao = db.getWordTypeTagRelationDao()
    private val relatedDao = db.getWordTypeTagRelatedWordDao()

    override suspend fun checkFileIFValid(fileData: MDFileData): Boolean {
        val valid = rawWordReader.validHeader(fileData)
        return valid
    }

    override fun processFile(
        fileData: MDFileData,
        existedWordStrategy: MDFileStrategy,
        corruptedWordStrategy: MDFileStrategy,
        onInvalidStream: () -> Unit,
        onUnsupportedFile: () -> Unit,
        onReadStreamError: (throwable: Throwable) -> Unit,
        tryGetReaderByMimeType: Boolean,
        tryGetReaderByFileHeader: Boolean,
    ): Flow<MDFileProcessingSummary> {
        return flow {
            val summaryHolder = RawWordSummaryHolder()
            try {
                db.withTransaction {
                    rawWordReader.readFile(
                        fileData = fileData,
                        onInvalidStream = {
                            Log.d(TAG, "invalid input stream for file $fileData")
                            onInvalidStream()
                            emit(summaryHolder.getEndSummary())
                        },
                        onUnsupportedFile = {
                            Log.d(TAG, "unsupported file type $fileData")
                            onUnsupportedFile()
                            emit(summaryHolder.getEndSummary())
                        },
                        onReadStreamError = {
                            Log.d(TAG, "read steam error $it cause: ${it.cause} for file $fileData")
                            onReadStreamError(it)
                            emit(summaryHolder.getEndSummary())
                        },
                        onComplete = {
                            Log.d(TAG, "read steam ended for file $fileData")
                            emit(summaryHolder.getEndSummary())
                        }
                    ) { word ->
                        Log.d(TAG, "read file, read valid word $word")
                        summaryHolder.update(word)

                        handleRawWord(word, existedWordStrategy, corruptedWordStrategy)
                        emit(summaryHolder.getOnGoingSummary())
                        true
                    }
                }
            } finally {
                emit(summaryHolder.getEndSummary())
                throw CancellationException("end flow")
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun MDImportFromFileRepoImpl.handleRawWord(
        word: MDRawWord,
        existedWordStrategy: MDFileStrategy,
        corruptedWordStrategy: MDFileStrategy,
    ) {
        val code = word.language.code
        if (code.valid) {
            val similarDbWord = getSimilarWordInDatabase(
                code = code,
                meaning = word.meaning,
                translation = word.translation
            )
            if (similarDbWord == null) {
                handleAddingNewRawWord(word)
            } else {
                handleAddingExistedRawWord(
                    word = word,
                    similarWord = similarDbWord,
                    strategy = existedWordStrategy
                )
            }
        } else {
            handleCorruptedRawWord(corruptedWordStrategy)
        }
    }

    private suspend fun getSimilarWordInDatabase(code: LanguageCode, meaning: String, translation: String): WordEntity? {
        val m = meaning.trim().lowercase()
        val t = translation.trim().lowercase()
        return wordDao.getWordsOfLanguage(code.code).map {
            it.firstOrNull { entity ->
                entity.meaning.trim().lowercase() == m && entity.translation.trim().lowercase() == t
            }
        }.first()
    }

    private suspend fun handleAddingNewRawWord(word: MDRawWord) {
        Log.d(TAG, "adding new raw word to database")

        ensureAddingLanguage(word.language)
        val typeTag = word.wordTypeTag?.let {
            handleAddingRawWordTypeTag(word.language, it)
        }
        val newWord = WordEntity(
            id = null,
            meaning = word.meaning,
            translation = word.translation,
            languageCode = word.language,
            additionalTranslations = word.additionalTranslations,
            tags = word.tags,
            transcription = word.transcription,
            examples = word.examples,
            wordTypeTagId = typeTag?.tag?.id,
        )
        val newWordID = wordDao.insertWord(newWord)
        typeTag?.let { tagWithRelation ->
            handleAddingRawWordRelations(newWordID, word, tagWithRelation)
        }
    }

    private suspend fun handleAddingExistedRawWord(
        word: MDRawWord,
        similarWord: WordEntity,
        strategy: MDFileStrategy,
    ) {
        when (strategy) {
            MDFileStrategy.Ignore -> {
                Log.d(TAG, "ignore existed word due to ignore existed word strategy")
            }

            MDFileStrategy.OverrideAll, MDFileStrategy.OverrideValid -> {
                val wordId = similarWord.id!!
                Log.d(TAG, "override existed word due to override existed word strategy")
                // TODO,

                val typeTag = word.wordTypeTag?.let {
                    handleAddingRawWordTypeTag(word.language, it)
                }

                val wordWithUpdatedField = handleOverrideFields(
                    rawWord = word,
                    dbWord = similarWord,
                    overrideAll = strategy == MDFileStrategy.OverrideAll,
                    typeTagId = typeTag?.tag?.id
                )
                wordDao.updateWord(wordWithUpdatedField)
                // delete realted word of this word
                relatedDao.deleteRelatedWordsOfWord(wordId)
                typeTag?.let { tagWithRelation ->
                    handleAddingRawWordRelations(
                        wordId = wordId,
                        word = word,
                        tagWithRelation = tagWithRelation
                    )
                }

            }

            MDFileStrategy.Abort -> {
                Log.d(TAG, "throw existed word exception due to abort existed word strategy")
                throw IllegalArgumentException("Found an existed word while parsing the file, aborting whole transaction")
            }
        }
    }

    private fun handleCorruptedRawWord(strategy: MDFileStrategy) {
        if (strategy == MDFileStrategy.Abort) {
            Log.d(TAG, "throw corrupted word exception due to abort corrupted word strategy")
            throw IllegalArgumentException("Found a corrupted word while parsing the file, aborting whole transaction")
        }
    }

    private suspend fun ensureAddingLanguage(code: String) {
        languageDao.insertLanguage(LanguageEntity(code))
    }


    private suspend fun handleAddingRawWordTypeTag(
        code: String,
        tag: MDRawWordTypeTag,
    ): TypeTagWithRelation {
        return getSimilarTypeTagInDatabase(code, tag.name) ?: let {
            ensureAddingLanguage(code)
            val newTypeTag = WordTypeTagEntity(name = tag.name, language = code)
            val id = tagDao.insertTagType(newTypeTag)
            TypeTagWithRelation(
                tag = WordTypeTagEntity(id = id, name = tag.name, language = code),
                relations = emptyList()
            )
        }
    }

    private suspend fun handleAddingRawWordRelations(wordId: Long, word: MDRawWord, tagWithRelation: TypeTagWithRelation) {
        val existedRelations = tagWithRelation.relations
        val tagId = tagWithRelation.tag.id!!
        word.wordTypeTag?.relations?.map { (label, relatedWord) ->
            val normalizedLabel = label.trim().lowercase()
            val relation: WordTypeTagRelationEntity = existedRelations.firstOrNull { entity ->
                entity.label.lowercase() == normalizedLabel
            } ?: let {
                val newRelation = WordTypeTagRelationEntity(id = null, label = label, tagId = tagId)
                // insert and get new id
                val newRelationId = relationDao.insertRelation(newRelation)
                // return new relation value with the new id
                newRelation.copy(id = newRelationId)
            }
            val newRelatedWord = WordTypeTagRelatedWordEntity(
                id = null,
                relationId = relation.id!!,
                baseWordId = wordId,
                word = relatedWord
            )
            relatedDao.insertRelatedWord(newRelatedWord)
        }
    }

    private suspend fun getSimilarTypeTagInDatabase(
        code: String,
        name: String,
    ): TypeTagWithRelation? {
        val normalizedName = name.trim().lowercase()
        return tagDao.getTagTypesOfLanguage(code).first().firstOrNull {
            it.tag.name.lowercase() == normalizedName
        }
    }

    private fun handleOverrideFields(
        rawWord: MDRawWord,
        dbWord: WordEntity,
        overrideAll: Boolean,
        typeTagId: Long?,
    ): WordEntity = if (overrideAll) {
        dbWord.copy(
            meaning = rawWord.meaning,
            translation = rawWord.translation,
            transcription = rawWord.transcription,
            additionalTranslations = rawWord.additionalTranslations,
            examples = rawWord.examples,
            tags = rawWord.tags,
            wordTypeTagId = typeTagId,
        )
    } else {
        dbWord.copy(
            meaning = rawWord.meaning.ifBlank { dbWord.meaning },
            translation = rawWord.translation.ifBlank { dbWord.translation },
            transcription = rawWord.transcription.ifBlank { dbWord.transcription },
            additionalTranslations = rawWord.additionalTranslations.ifEmpty { dbWord.additionalTranslations },
            examples = rawWord.examples.ifEmpty { dbWord.examples },
            tags = rawWord.tags.ifEmpty { dbWord.tags },
            wordTypeTagId = typeTagId,
        )
    }
}

private class RawWordSummaryHolder {
    val words = mutableSetOf<Pair<String, String>>() // meaning to translation
    val languages = mutableSetOf<String>()
    val tags = mutableSetOf<String>()
    val wordTypeTags = mutableSetOf<String>()
    val wordTypeTagRelation = mutableSetOf<String>()
    var i = 0
    fun getOnGoingSummary() = MDFileProcessingSummary(
        wordsCount = words.count(),
        languagesCount = languages.count(),
        tagsCount = tags.count(),
        wordTypeTagCount = wordTypeTags.count(),
        wordTypeTagRelationCount = wordTypeTagRelation.count(),
        totalEntriesRead = i++,
        status = MDFileProcessingSummaryStatus.RUNNING,
    )

    fun getEndSummary() = MDFileProcessingSummary(
        wordsCount = words.count(),
        languagesCount = languages.count(),
        tagsCount = tags.count(),
        wordTypeTagCount = wordTypeTags.count(),
        wordTypeTagRelationCount = wordTypeTagRelation.count(),
        totalEntriesRead = i,
        status = MDFileProcessingSummaryStatus.COMPLETED,
    )

    fun update(word: MDRawWord) {
        // update summaries
        words.add(word.meaning to word.translation)
        languages.add(word.language)
        tags.addAll(word.tags)
        word.wordTypeTag?.let {
            wordTypeTags.add(it.name)
            wordTypeTagRelation.addAll(it.relations.map { it.label })
        }
    }
}
