package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.exception.CloseTransactionException
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.MDCSVFileSplitter
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.FileProcessingSummaryErrorType
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingMutableSummaryActions
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.languageOrNull
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDImportFromFileRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val TAG = "file_reader"

class MDImportFromFileRepoImpl(
    private val db: MDDataBase,
    private val rawWordReader: MDFileReaderDecorator<MDRawWord>,
    private val rawWordCSVFileSplitter: MDCSVFileSplitter<MDRawWord>,
) : MDImportFromFileRepo {
    private val wordDao = db.getWordDao()
    private val tagDao = db.getWordTypeTagDao()
    private val languageDao = db.getLanguageDao()
    private val relationDao = db.getWordTypeTagRelationDao()
    private val relatedDao = db.getWordTypeTagRelatedWordDao()

    override suspend fun checkFileIfValid(fileData: MDFileData): Boolean {
        val valid = rawWordReader.validHeader(fileData)
        return valid
    }

    override suspend fun processFile(
        fileData: MDFileData,
        outputSummaryActions: MDFileProcessingMutableSummaryActions,
        existedWordStrategy: MDFileStrategy,
        corruptedWordStrategy: MDFileStrategy,
        tryGetReaderByMimeType: Boolean,
        tryGetReaderByFileHeader: Boolean,
        allowedLanguages: Set<Language>,
    ) = withContext(Dispatchers.IO) {
        outputSummaryActions.onSplitFile()
        val splittedFilesData: Map<String, MDFileData> = splitInputFile(
            allowedLanguages = allowedLanguages,
            fileData = fileData,
            onRecognizeLanguage = { language ->
                // ensure adding language
                val isNewLanguage = !languageDao.hasLanguage(language.code.code)
                if (isNewLanguage) {
                    languageDao.insertLanguage(LanguageEntity(language.code.code))
                }

                outputSummaryActions.onRecognizeLanguage(language, isNewLanguage)
            }
        )
        splittedFilesData.forEach { (languageCode, fileData) ->
            val language = languageCode.code.language
            parseFileData(
                language = language,
                fileData = fileData,
                outputSummaryActions = outputSummaryActions,
                existedWordStrategy = existedWordStrategy,
                corruptedWordStrategy = corruptedWordStrategy,
                tryGetReaderByMimeType = tryGetReaderByMimeType,
                tryGetReaderByFileHeader = tryGetReaderByFileHeader
            )
        }
    }

    private suspend fun parseFileData(
        language: Language,
        fileData: MDFileData,
        outputSummaryActions: MDFileProcessingMutableSummaryActions,
        existedWordStrategy: MDFileStrategy,
        corruptedWordStrategy: MDFileStrategy,
        tryGetReaderByMimeType: Boolean,
        tryGetReaderByFileHeader: Boolean,
    ) {
        // language is already in the database for sure
        outputSummaryActions.onSetCurrentLanguage(language)
        outputSummaryActions.onScanDatabase()

        /** k: tag name normalized, v: tag id */
        val allTypeTags = mutableMapOf<String, Long>()

        /** k: relation label normalized, v: [tag id to label id] (name is not unique) */
        val allTypeTagsRelations = mutableMapOf<String, MutableSet<Pair<Long, Long>>>()

        /** k: relation id, v: relation label (not normalized) */
        val allTypeTagsRelationsLabels = mutableMapOf<Long, String>()
        initTagsAndRelationsOfLanguage(
            language = language,
            allTypeTags = allTypeTags,
            allTypeTagsRelations = allTypeTagsRelations,
            allTypeTagsRelationsLabels = allTypeTagsRelationsLabels
        )

        /** k: (normalized meaning, normalized translation), k: word id */
        val allWords = mutableMapOf<Pair<String, String>, Long>()
        initWordsOfLanguages(language, allWords)

        val allWordsTags = wordDao.getTagsInLanguage(language.code.code).first().flatMap {
            StringListConverter.stringToListConverter(it) // decode it as it is decoded in the first place
        }.toSet()

        /**
         *  k: word id,
         *  v: map of word relations {
         *      k: (normalized relation label, normalized related word)
         *      v: related word id
         *  }
         */
        val allRelatedWords: MutableMap<Long, MutableMap<Pair<String, String>, Long>> = mutableMapOf()
        initRelatedWordsOfLanguage(allWords, allTypeTagsRelationsLabels, allRelatedWords)

        outputSummaryActions.onStoreData()
        db.withTransaction {
            rawWordReader.readFile(
                fileData = fileData,
                onInvalidStream = {
                    outputSummaryActions.onError(FileProcessingSummaryErrorType.INVALID_STREAM)
                },
                onUnsupportedFile = {
                    outputSummaryActions.onError(FileProcessingSummaryErrorType.UNSUPPORTED_FILE)
                },
                onReadStreamError = {
                    outputSummaryActions.onError(FileProcessingSummaryErrorType.CORRUPTED_FILE)
                },
                tryGetReaderByMimeType = tryGetReaderByMimeType,
                tryGetReaderByFileHeader = tryGetReaderByFileHeader,
                onComplete = {
                    outputSummaryActions.onComplete()
                },
            ) { rawWord ->
                outputSummaryActions.recognizeRawWord()
                val rawWordIdentifier = Pair(
                    first = rawWord.meaning.trim().lowercase(),
                    second = rawWord.translation.trim().lowercase(),
                )
                val isValidWord = rawWord.meaning.isNotBlank() && rawWord.translation.isNotBlank() && rawWord.language.code.languageOrNull != null
                val isNewWord = rawWordIdentifier !in allWords
                val handleWord = if (!isValidWord) {
                    if (corruptedWordStrategy == MDFileStrategy.Abort) {
                        throw CloseTransactionException
                    }
                    outputSummaryActions.recognizeCorruptedWord()
                    false
                } else if (!isNewWord) {
                    when (existedWordStrategy) {
                        MDFileStrategy.Ignore -> false
                        MDFileStrategy.OverrideAll,
                        MDFileStrategy.OverrideValid,
                            -> true

                        MDFileStrategy.Abort -> throw CloseTransactionException
                    }.also {
                        if (it) {
                            outputSummaryActions.recognizeUpdatedWord()
                        } else {
                            outputSummaryActions.recognizeIgnoredWord()
                        }
                    }
                } else {
                    outputSummaryActions.recognizeNewWord()
                    true
                }
                if (handleWord) {
                    // type tag and relations
                    val tagId: Long? = rawWord.wordTypeTag?.let { tag ->
                        val tagIdentifier = tag.name.trim().lowercase()
                        val tagId: Long = allTypeTags.getOrPut(tagIdentifier) {
                            tagDao.insertTagType(
                                WordTypeTagEntity(id = null, name = tag.name, language = language.code.code)
                            ).also {
                                outputSummaryActions.recognizeNewTypeTag()
                            }
                        }
                        tagId
                    }

                    // word entity
                    val wordId = allWords.getOrPut(rawWordIdentifier) {
                        wordDao.insertWord(
                            WordEntity(
                                id = null,
                                meaning = rawWord.meaning,
                                translation = rawWord.translation,
                                languageCode = language.code.code,
                                additionalTranslations = rawWord.additionalTranslations.filter { it.isNotBlank() },
                                tags = rawWord.tags.filter { it.isNotBlank() },
                                wordTypeTagId = tagId,
                                learningProgress = 0f,
                                transcription = rawWord.transcription.ifBlank { "" },
                                examples = rawWord.examples.filter { it.isNotBlank() },
                                createdAt = System.currentTimeMillis()
                            )
                        )
                    }
                    if (!isNewWord) {
                        val entityWord = wordDao.getWord(wordId)!!
                        val overrideAll = existedWordStrategy == MDFileStrategy.OverrideAll
                        val newEntityWord = handleOverrideEntityWordsFromRawWord(
                            rawWord = rawWord,
                            dbWord = entityWord,
                            overrideAll = overrideAll,
                            typeTagId = tagId
                        )
                        wordDao.updateWord(newEntityWord)
                    }
                    // word tags:
                    outputSummaryActions.recognizeTags(
                        newRecognizedTags = rawWord.tags - allWordsTags
                    )

                    // related words
                    if (tagId != null) {
                        val relatedWordsMap: MutableMap<Pair<String, String>, Long> = allRelatedWords.getOrPut(wordId) { mutableMapOf() }
                        rawWord.wordTypeTag.relations.forEach { relation ->
                            val relationIdentifier = relation.label.trim().lowercase()

                            // relation
                            val relationId: Long = allTypeTagsRelations.getOrPut(relationIdentifier) {
                                mutableSetOf()
                            }.let { tagsToRelations ->
                                // the relation must be one per tag, so this check (does this relation is in the current tag
                                // is enough to check if this relation is existed or not
                                tagsToRelations.firstNotNullOfOrNull { (tId, rId) ->
                                    if (tId == tagId) {
                                        rId
                                    } else {
                                        null
                                    }
                                } ?: let {
                                    // this relation is not existed in this tag so we insert it then we add it to the set
                                    outputSummaryActions.recognizeNewTypeTagRelation()
                                    val relationId = relationDao.insertRelation(
                                        WordTypeTagRelationEntity(id = null, label = relation.label, tagId = tagId)
                                    )
                                    tagsToRelations.add(tagId to relationId)
                                    relationId
                                }
                            }

                            val relatedWordIdentifier = Pair(
                                first = relationIdentifier,
                                second = relation.relatedWord.trim().lowercase()
                            )
                            // if the relation of this word is not existed then we add it
                            // maybe we don't need this related word
                            val relatedWordId = relatedWordsMap.getOrPut(relatedWordIdentifier) {
                                relatedDao.insertRelatedWord(
                                    WordTypeTagRelatedWordEntity(
                                        id = null,
                                        relationId = relationId,
                                        baseWordId = wordId,
                                        word = relation.relatedWord
                                    )
                                )
                            }
                            // related
                            relation.relatedWord
                        }
                    }
                }
                true
            }
        }
    }

    private suspend fun initRelatedWordsOfLanguage(
        allWords: MutableMap<Pair<String, String>, Long>,
        allTypeTagsRelationsLabels: MutableMap<Long, String>,
        allRelatedWords: MutableMap<Long, MutableMap<Pair<String, String>, Long>>,
    ) {
        relatedDao.getAllRelatedWordsOfWords(allWords.values.toSet()).first().forEach { relatedWord ->
            val normalizedIdentifier: Pair<String, String> = Pair(
                first = allTypeTagsRelationsLabels[relatedWord.relationId]!!.trim().lowercase(),
                second = relatedWord.word.trim().lowercase()
            )
            allRelatedWords.getOrPut(relatedWord.baseWordId) {
                mutableMapOf()
            }[normalizedIdentifier] = relatedWord.id!!
        }
    }

    private suspend fun initWordsOfLanguages(
        language: Language,
        allWords: MutableMap<Pair<String, String>, Long>,
    ) {
        wordDao.getWordsOfLanguage(language.code.code).first().forEach { word ->
            val normalizedWordIdentifier = Pair(
                first = word.meaning.meaningSearchNormalize,
                second = word.translation.meaningSearchNormalize
            )
            allWords[normalizedWordIdentifier] = word.id!!
        }
    }

    private suspend fun initTagsAndRelationsOfLanguage(
        language: Language,
        allTypeTags: MutableMap<String, Long>,
        allTypeTagsRelations: MutableMap<String, MutableSet<Pair<Long, Long>>>,
        allTypeTagsRelationsLabels: MutableMap<Long, String>,
    ) {
        tagDao.getTagTypesOfLanguage(language.code.code).first().forEach { tagWithRelation ->
            allTypeTags[tagWithRelation.tag.name.tagMatchNormalize] = tagWithRelation.tag.id!!
            tagWithRelation.relations.forEach { relation ->
                val normalizedLabel = relation.label.trim().lowercase()
                allTypeTagsRelations.getOrPut(
                    key = normalizedLabel
                ) {
                    mutableSetOf()
                }.add(tagWithRelation.tag.id to relation.id!!)

                allTypeTagsRelationsLabels[relation.id] = relation.label
            }
        }
    }

    private inline fun splitInputFile(
        allowedLanguages: Set<Language>,
        fileData: MDFileData,
        onRecognizeLanguage: (Language) -> Unit,
    ): Map<String, MDFileData> {
        val allowedLanguagesCodes = allowedLanguages.map { it.code.code }.toSet()

        return rawWordCSVFileSplitter.splitFile(
            file = fileData,
            splitter = { word ->
                word.language.code.also {
                    onRecognizeLanguage(it.language)
                }.code

            },
            filter = { word -> word.language in allowedLanguagesCodes },
        )
    }

    private fun handleOverrideEntityWordsFromRawWord(
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


