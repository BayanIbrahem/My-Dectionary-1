package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.and
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileWordPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.validateWith
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.validateWithDatabase
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFilePartReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFileReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFileReaderAbstractFactory
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictException
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionException
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActions
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryStepException
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryStepWarning
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.ImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceState
import kotlinx.coroutines.flow.first

class MDRoomImportFromFileRepo(
    private val db: MDDataBase,
    private val abstractFactory: MDFileReaderAbstractFactory,
    private val appVersion: String,
) : ImportFromFileRepo {
    private val languageDao: LanguageDao = db.getLanguageDao()
    private val wordClassDao: WordClassDao = db.getWordClassDao()
    private val typeRelationDao: WordClassRelationWordsDao = db.getWordClassRelationDao()
    private val contextTagDao: ContextTagDao = db.getContextTagDao()
    private val wordDao: WordDao = db.getWordDao()
    private val wordWithContextTagsAndRelatedWordsDao = db.getWordsWithContextTagAndRelatedWordsDao()
    private val wordCrossTagsDao = db.getWordsCrossTagsDao()
    private val relatedWordDao: WordClassRelatedWordDao = db.getWordClassRelatedWordDao()

    override suspend fun checkFileIfValid(fileData: MDDocumentData): Boolean {
        val factory = abstractFactory.getFirstSuitableFactoryOrNull(fileData)
        return factory != null
    }

    private suspend fun safeGetFileReader(
        fileData: MDDocumentData,
        outputSummaryActions: MDFileProcessingSummaryActions,
    ): MDFileReader? {
        outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.RecognizingFileType)
        val factory = abstractFactory.getFirstSuitableFactoryOrNull(fileData)

        if (factory == null) {
            outputSummaryActions.onException(MDFileProcessingSummaryStepException.UnRecognizedFileType(appVersion))
            return null
        }

        outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.RecognizingFileReader)
        return try {
            factory.buildReaderForData(fileData)
        } catch (e: Exception) {
            outputSummaryActions.onException(
                stepException = MDFileProcessingSummaryStepException.UnRecognizedFileReader(
                    appVersion = appVersion, fileVersion = factory.getVersionOrNull(fileData), availableFilesVersions = factory.availableVersions
                ),
            )
            null
        }
    }

    override suspend fun getAvailablePartsInFile(
        fileData: MDDocumentData,
        outputSummaryActions: MDFileProcessingSummaryActions,
    ): List<MDFilePartType> {
        val fileReader = safeGetFileReader(fileData, outputSummaryActions) ?: return emptyList()

        return safeGetAvailableParts(
            fileData,
            fileReader,
            outputSummaryActions,
        )
    }

    private suspend fun safeGetAvailableParts(
        fileData: MDDocumentData,
        fileReader: MDFileReader,
        outputSummaryActions: MDFileProcessingSummaryActions,
    ): List<MDFilePartType> {
        outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.GetAvailableParts)
        return try {
            fileReader.getAvailablePartsOfFile(fileData)
        } catch (e: Exception) {
            outputSummaryActions.onException(MDFileProcessingSummaryStepException.UnableToGetFileParts)
            return emptyList()
        }
    }

    private class FileProcessingScope(
        val outputSummaryActions: MDFileProcessingSummaryActions,
        val existedWordStrategy: MDPropertyConflictStrategy,
        val corruptedWordStrategy: MDPropertyCorruptionStrategy,
        val fileReader: MDFileReader,
        val allowedParts: Set<MDFilePartType>,
        val extraTags: List<ContextTag>,
        val extraTagsStrategy: MDExtraTagsStrategy,
    ) {
        val languagesWordSpaces = mutableMapOf<Language, List<WordClass>>()
        val allLanguages = mutableSetOf<String>()
        val wordClassNameMapper = mutableMapOf<String, MutableMap<String, Long>>()
        val typeRelationNameMapper = mutableMapOf<String, MutableMap<Long, Long>>()
        val contextTagNameMapper = mutableMapOf<String, Long>()
    }

    override suspend fun processFile(
        fileData: MDDocumentData,
        outputSummaryActions: MDFileProcessingSummaryActions,
        existedWordStrategy: MDPropertyConflictStrategy,
        corruptedWordStrategy: MDPropertyCorruptionStrategy,
        extraTags: List<ContextTag>,
        extraTagsStrategy: MDExtraTagsStrategy,
        allowedFileParts: Set<MDFilePartType>,
    ) {
        outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.Start)
        val fileReader = safeGetFileReader(fileData, outputSummaryActions) ?: return
        val availableParts = safeGetAvailableParts(
            fileData = fileData, fileReader = fileReader, outputSummaryActions = outputSummaryActions
        ).toSet()
        val allowedParts = allowedFileParts.and(availableParts)

        if (allowedParts.isEmpty()) {
            outputSummaryActions.onWarning(MDFileProcessingSummaryStepWarning.BlankValidParts)
            return
        }

        val scope = FileProcessingScope(
            outputSummaryActions = outputSummaryActions,
            existedWordStrategy = existedWordStrategy,
            corruptedWordStrategy = corruptedWordStrategy,
            fileReader = fileReader,
            allowedParts = allowedParts,
            extraTags = extraTags,
            extraTagsStrategy = extraTagsStrategy,
        )

        processLanguages(scope)

        try {
            db.withTransaction {
                insertLanguages(scope)
                processWordsClasses(scope)
                freeLanguageMemoryIfNotRequired(scope)
                processContextTags(scope)
                freeContextTagsMemoryIfNotRequired(scope)
                processWords(scope)
            }
        } catch (e: Exception) {

        }

        scope.outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.End)
    }

    /**
     * read languages with words classes from file as a list of [LanguageWordSpaceState]
     * no need to merge any duplicated word spaces cause all words classes that have same language and same name would be treated as a single word class
     * in [processSingleTag] and the same is for tag relations withen them
     */
    private suspend fun processLanguages(scope: FileProcessingScope) {
        if (MDFilePartType.Language in scope.allowedParts) {
            scope.outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.ParseSaveLanguages)
            val languageReader = scope.fileReader.getReaderOfPart(MDFilePartType.Language) as MDFilePartReader.Language
            val fileLanguages = languageReader.readFile().associate {
                it.toLanguage() to it.toWordsClasses()
            }.toList()
            if (fileLanguages.isEmpty()) {
                scope.outputSummaryActions.onWarning(MDFileProcessingSummaryStepWarning.BlankLanguages)
            }
            scope.languagesWordSpaces.putAll(fileLanguages)
        }
    }

    /**
     * insert all parsed languages to database (only languages not words classes) and prepare all languages set that store ids of all installed languages
     */
    private suspend fun insertLanguages(
        scope: FileProcessingScope,
    ) {
        val dbLanguages = languageDao.getAllLanguages().first().map { it.code }

        languageDao.insertAllLanguages(languages = scope.languagesWordSpaces.map { (language) ->
            LanguageEntity(language.code)
        })
        scope.languagesWordSpaces.forEach { (code) ->
            scope.outputSummaryActions.recognizeLanguage(
                code = code, new = code.code !in dbLanguages
            )
        }

        scope.allLanguages.addAll(languageDao.getAllLanguages().first().map { it.code })
    }

    /**
     * store all words classes
     * - for each language we get all stored words classes in a mutable collection
     * - then we process each tag on its own using [processSingleTag]
     */
    private suspend fun processWordsClasses(
        scope: FileProcessingScope,
    ) {
        scope.outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.ParseAndSaveWordsClasses)
        scope.languagesWordSpaces.forEach { (space, tags) ->
            val code = space.code
            val tagsOfLanguage = wordClassDao.getTagTypesOfLanguage(code).first().associate { (tag, relations) ->
                tag.name to (tag.id!! to relations.associate { it.label to it.id!! }.toMutableMap())
            }.toMutableMap()

            tags.forEach { tagData ->
                processSingleTag(scope, code, tagData, tagsOfLanguage)
            }
        }
    }

    private suspend fun freeLanguageMemoryIfNotRequired(scope: FileProcessingScope) {
        if (MDFilePartType.Word !in scope.allowedParts) { // no need for cache data of languages
            scope.typeRelationNameMapper.clear()
            scope.typeRelationNameMapper.clear()
            scope.allLanguages.clear()
            scope.languagesWordSpaces.clear()
        }
    }

    /**
     * process each word class on each own
     * - try to get a tag that has the same name from the db
     * - then we get the db tag id either from that db tag or by inserting new tag in the db
     * - after that we update the cached ids and cached names for next tags
     * - we get tag relations from db version of it (empty set if it is a new tag)
     * - then we walk throw each tag relation from the parsed data and try to get db version of it
     * which has the same name to get its id or insert new one in the database
     * - we update cached id and name maps for next relation
     * - if tags with the same name appeared in the same word space or two word spaces has the same language code
     * so they would be merged smartly according to each tag name and also relations would be merged the same way
     * and for words which use the *NOT FIRST* tag by its id would be able reach the first tag that appeared with
     * its name by name cache map
     */
    private suspend fun processSingleTag(
        scope: FileProcessingScope,
        languageCode: String,
        tagData: WordClass,
        tagsOfLanguage: MutableMap<String, Pair<Long, MutableMap<String, Long>>>,
    ) {
        if (tagData.name.isBlank()) {
            // TODO, handle facing invalid tag name
            return
        }
        val dbTag = tagsOfLanguage[tagData.name.lowercase()]
        val tagId = dbTag?.first ?: wordClassDao.insertTagType(tagData.asTagEntity(null))
        scope.outputSummaryActions.recognizeWordClass(
            languageCode = languageCode.code, name = tagData.name, new = dbTag == null
        )

        scope.wordClassNameMapper.getOrPut(tagData.name.lowercase()) { mutableMapOf() }[languageCode] = tagId

        val dbTagRelations = dbTag?.second ?: mutableMapOf()

        tagData.relations.forEach { relation ->
            if (relation.label.isNotBlank()) {
                val relationId = dbTagRelations[relation.label.lowercase()]?.also {
                    scope.outputSummaryActions.recognizeWordClassRelation(
                        languageCode = languageCode.code, wordClassName = tagData.name, relationLabel = relation.label, new = false
                    )
                } ?: let {
                    scope.outputSummaryActions.recognizeWordClassRelation(
                        languageCode = languageCode.code, wordClassName = tagData.name, relationLabel = relation.label, new = true
                    )
                    typeRelationDao.insertRelation(relation.asRelationEntity(tagId, null))
                }
                scope.typeRelationNameMapper.getOrPut(relation.label.lowercase()) { mutableMapOf() }[tagId] = relationId
            } else {
                // TODO, facing a word class relation with invalid name
            }
        }
        tagsOfLanguage[tagData.name.lowercase()] = tagId to dbTagRelations
    }

    /**
     * parse context tags from file and store them with name and id cache map for late uses
     */
    private suspend fun processContextTags(scope: FileProcessingScope) {
        if (MDFilePartType.Tag !in scope.allowedParts) return
        scope.outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.ParseAndSaveContextTags)

        val tagsReader = scope.fileReader.getReaderOfPart(MDFilePartType.Tag) as MDFilePartReader.Tag
        val contextTags = tagsReader.readFile().map { it.toContextTag() }
        if (contextTags.firstOrNull() == null) { // empty
            scope.outputSummaryActions.onWarning(MDFileProcessingSummaryStepWarning.BlankContextTags)
            return
        }

        val dbTags = contextTagDao.getAllContextTags().first().associate { it.path.lowercase() to it.tagId!! }.toMutableMap()
        scope.contextTagNameMapper.putAll(dbTags)
        contextTags.forEach { tag ->
            if (tag == null) {
                // TODO, facing invalid context tag
            } else {
                val tagId = dbTags[tag.value.lowercase()]?.also {
                    scope.outputSummaryActions.recognizeContextTag(tag.value, false)
                } ?: let {
                    scope.outputSummaryActions.recognizeContextTag(tag.value, true)
                    contextTagDao.insertContextTag(tag.asEntity(null))
                }
                scope.contextTagNameMapper[tag.value.lowercase()] = tagId
            }
        }
    }

    private suspend fun freeContextTagsMemoryIfNotRequired(scope: FileProcessingScope) {
        if (MDFilePartType.Word !in scope.allowedParts) { // no need for cache data of languages
            scope.contextTagNameMapper.clear()
        }
    }

    /**
     * this function process words list
     * at first try to parse words sequence from file
     * then process each entry in [processWordPart]
     */
    private suspend fun processWords(scope: FileProcessingScope) {
        if (MDFilePartType.Word !in scope.allowedParts) return
        scope.outputSummaryActions.onStep(MDFileProcessingSummaryActionsStep.ParseAndSaveWords)

        val wordReader = scope.fileReader.getReaderOfPart(MDFilePartType.Word) as MDFilePartReader.Word
        wordReader.readFile().forEach { wordPart ->
            processWordPart(scope, wordPart)
        }
    }

    /**
     * at first try to get similar word data from db
     * then generate word model using provided [wordPart] and similar word data in db according to [MDPropertyConflictStrategy]
     */
    private suspend fun processWordPart(scope: FileProcessingScope, wordPart: MDFileWordPart) {
        val dbWordId = fetchDbWordId(wordPart)
        val dbWordSource = fetchDbWordSource(dbWordId)

        val isNewLanguage = wordPart.language !in scope.allLanguages
        if (isNewLanguage) {
            languageDao.insertLanguage(LanguageEntity(code = wordPart.language))
            scope.allLanguages.add(wordPart.language)
        }

        scope.outputSummaryActions.recognizeLanguage(wordPart.language.code, isNewLanguage)
        scope.outputSummaryActions.recognizeWord(wordPart.language.code, wordPart.meaning, wordPart.translation, dbWordId == null)

        val word = try {
            wordPart.toWord().validateWithDatabase(
                dbWord = dbWordSource,
                dbWordStrategy = scope.existedWordStrategy,
                corruptedStrategy = scope.corruptedWordStrategy,
                getDBContextTag = { contextTagData ->
                    resolveContextTag(scope, contextTagData)
                },
                getDBWordClass = { wordClassData ->
                    resolveWordClass(scope, wordClassData)
                },
            )
        } catch (e: MDPropertyCorruptionException) {
            when (e) {
                MDPropertyCorruptionException.AbortTransaction -> {
                    scope.outputSummaryActions.onException(MDFileProcessingSummaryStepException.CorruptedWordTransactionAbort)
                    throw e
                }

                MDPropertyCorruptionException.AbortWord -> {
                    scope.outputSummaryActions.onWarning(MDFileProcessingSummaryStepWarning.CorruptedWordAbort)
                    null
                }
            }
        } catch (e: MDPropertyConflictException) {
            when (e) {
                MDPropertyConflictException.AbortTransaction -> {
                    scope.outputSummaryActions.onException(MDFileProcessingSummaryStepException.ExistedWordTransactionAbort)
                    scope.outputSummaryActions.recognizeCorruptedWord(
                        languageCode = wordPart.language.code, meaning = wordPart.meaning, translation = wordPart.translation, new = dbWordId == null
                    )
                    throw e
                }

                MDPropertyConflictException.AbortWord -> {
                    scope.outputSummaryActions.onWarning(MDFileProcessingSummaryStepWarning.ExistedWordAbort)
                    scope.outputSummaryActions.recognizeCorruptedWord(
                        languageCode = wordPart.language.code, meaning = wordPart.meaning, translation = wordPart.translation, new = dbWordId == null
                    )
                    null
                }
            }
        }

        if (word == null) return
        val wordId = handleWordEntity(scope, word)
        handleWordExtraTags(
            scope = scope,
            word = word,
            newId = wordId,
        )
        handleRelatedWords(
            scope = scope, word = word.copy(id = wordId)
        )
    }

    private suspend fun handleWordExtraTags(
        scope: FileProcessingScope,
        word: Word,
        newId: Long,
    ) {
        if (scope.extraTags.isNotEmpty()) {
            val shouldAdd = when (scope.extraTagsStrategy) {
                MDExtraTagsStrategy.New -> word.id == INVALID_ID
                MDExtraTagsStrategy.Updated -> word.id != INVALID_ID
                MDExtraTagsStrategy.All -> true
            }
            if (shouldAdd) {
                wordCrossTagsDao.insertWordCrossContextTagsList(
                    wordCrossTag = scope.extraTags.map {
                        WordCrossContextTagEntity(id = null, wordId = newId, tagId = it.id)
                    }
                )
            }
        }
    }

    /**
     * get id of similar word in the database if exists
     */
    private suspend fun fetchDbWordId(wordPart: MDFileWordPart): Long? {
        return wordDao.getWord(
            meaning = wordPart.meaning, translation = wordPart.translation, languageCode = wordPart.language
        )?.id
    }

    /** get similar word data if exists */
    private suspend fun fetchDbWordSource(dbWordId: Long?): Word? {
        return dbWordId?.let {
            wordWithContextTagsAndRelatedWordsDao.getWordWithContextTagsAndRelatedWordsRelation(it)
        }?.asWordModel()
    }

    /**
     * get context tag of the word
     * - searching first using local id with the id cache map
     * - searching using local name with name cache map
     * - if none of its id/name exists then it store a new context tag (this part would be used the most if no languages provided before)
     */
    private suspend fun resolveContextTag(
        scope: FileProcessingScope,
        providedContextTagData: ContextTag,
    ): ContextTag {
        return scope.contextTagNameMapper[providedContextTagData.value.lowercase()]?.let { id ->
            contextTagDao.getContextTag(id)?.asModel()?.also {
                scope.outputSummaryActions.recognizeContextTag(it.value, false)
            }
        } ?: createNewContextTag(scope, providedContextTagData)
    }

    /**
     * create a new tag id and cache it in id/name cache maps for context tags
     */
    private suspend fun createNewContextTag(
        scope: FileProcessingScope,
        providedContextTagData: ContextTag,
    ): ContextTag {
        val newTagId = contextTagDao.insertContextTag(providedContextTagData.asEntity(null))
        scope.contextTagNameMapper[providedContextTagData.value.lowercase()] = newTagId
        scope.outputSummaryActions.recognizeContextTag(providedContextTagData.value, true)
        return providedContextTagData.copy(id = newTagId)
    }

    /**
     * get word class of the word
     * - searching first using local id with the id cache map
     * - searching using local name with name cache map
     * - if none of its id/name exists then it store a new word class (this part would be used the most if no languages provided before)
     */
    private suspend fun resolveWordClass(
        scope: FileProcessingScope,
        providedWordClassData: WordClass,
    ): WordClass {
        return let {
            scope.wordClassNameMapper[providedWordClassData.name.lowercase()]?.get(providedWordClassData.language.code)
        }?.let { id ->
            wordClassDao.getTagType(id)?.asTagModel()
        } ?: createNewWordClass(scope, providedWordClassData)
    }

    /**
     * create a new tag id and cache it in id/name cache maps for words classes
     */

    private suspend fun createNewWordClass(
        scope: FileProcessingScope,
        providedWordClassData: WordClass,
    ): WordClass {
        val dbTagId = wordClassDao.insertTagType(
            tag = providedWordClassData.asTagEntity(null)
        )
        scope.wordClassNameMapper.getOrPut(providedWordClassData.name.lowercase()) {
            mutableMapOf()
        }[providedWordClassData.language.code] = dbTagId
        return providedWordClassData.copy(id = dbTagId)
    }

    /**
     * insert or update word
     * insert context tags with word at this point every tag id in the [word] must be valid db id
     */
    private suspend fun handleWordEntity(scope: FileProcessingScope, word: Word): Long {
        val wordEntity = word.asWordEntity(false)
        val id = if (wordEntity.id == null) {
            wordDao.insertWord(wordEntity)
        } else {
            wordDao.updateWord(wordEntity)
            word.id
        }
        if (word.tags.isNotEmpty()) {
            val tagsRelations = word.tags.map { WordCrossContextTagEntity(tagId = it.id, wordId = id) }
            wordCrossTagsDao.insertWordCrossContextTagsList(tagsRelations)
        }

        return id
    }

    /**
     * handle related words of word
     */
    private suspend fun handleRelatedWords(
        scope: FileProcessingScope,
        word: Word,
    ) {
        if (word.wordClass != null) {
            val relatedWordData = word.relatedWords.map { relatedWord ->
                val relationId = resolveRelationId(
                    scope = scope, language = word.language,
                    wordClassId = word.wordClass.id,
                    wordClassName = word.wordClass.name,
                    relatedWord = relatedWord,
                )
                WordClassRelatedWordEntity(
                    id = null, relationId = relationId, baseWordId = word.id, word = relatedWord.value
                )
            }
            relatedWordDao.deleteRelatedWordsOfWord(word.id)
            relatedWordDao.insertRelatedWords(relatedWordData)
        }
    }

    private suspend fun resolveRelationId(
        scope: FileProcessingScope,
        language: LanguageCode,
        wordClassId: Long,
        wordClassName: String,
        relatedWord: RelatedWord,
    ): Long {
        return let {
            scope.typeRelationNameMapper[relatedWord.relationLabel.lowercase()]?.get(wordClassId)
        }?.also {
            scope.outputSummaryActions.recognizeWordClassRelation(
                languageCode = language, wordClassName = wordClassName, relationLabel = relatedWord.relationLabel, new = false
            )
        } ?: createNewRelation(
            scope = scope,
            language = language,
            wordClassId = wordClassId,
            wordClassName = wordClassName,
            label = relatedWord.relationLabel,
        )
    }

    private suspend fun createNewRelation(
        scope: FileProcessingScope,
        language: LanguageCode,
        wordClassId: Long,
        wordClassName: String,
        label: String,
    ): Long {
        val dbRelationId = typeRelationDao.insertRelation(
            relation = WordClassRelationEntity(
                id = null,
                label = label,
                tagId = wordClassId,
            )
        )
        scope.typeRelationNameMapper.getOrPut(label.lowercase()) { mutableMapOf() }[wordClassId] = dbRelationId

        scope.outputSummaryActions.recognizeWordClassRelation(
            languageCode = language, wordClassName = wordClassName, relationLabel = label, new = true
        )

        return dbRelationId
    }
}