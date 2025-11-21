package dev.bayan_ibrahim.my_dictionary.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfNegative
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.tag.TagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordsPaginatedDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_tag.WordsCrossTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordClassModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class MDRoomWordRepo(
    private val db: MDDataBase,
) : WordRepo {
    private val wordDao: WordDao = db.getWordDao()
    private val wordsPaginatedDao: WordsPaginatedDao = db.getWordsPaginatedDao()
    private val wordWithTagsDao: WordWithTagDao = db.getWordWithTagDao()
    private val wordWithTagsAndRelatedWordsDao: WordWithTagsAndRelatedWordsDao = db.getWordsWithTagAndRelatedWordsDao()
    private val wordsCrossTagDao: WordsCrossTagDao = db.getWordsCrossTagsDao()
    private val wordClassDao: WordClassDao = db.getWordClassDao()
    private val languageDao: LanguageDao = db.getLanguageDao()
    private val tagDao: TagDao = db.getTagDao()

    private fun <T : Any, K : Any> pagingDataOf(
        mapper: suspend (T) -> K,
        pagingSourceFactory: () -> PagingSource<Int, T>,
    ): Flow<PagingData<K>> = Pager(
        config = PagingConfig(
            pageSize = 50,
            prefetchDistance = 50,
        ),
        pagingSourceFactory = pagingSourceFactory
    ).flow.map {
        it.map(mapper)
    }

    private fun checkMatchMemorizingProbabilityOf(
        progress: Float,
        filterProgressGroups: Collection<MDWordsListMemorizingProbabilityGroup>,
    ): Boolean = filterProgressGroups.isEmpty() || filterProgressGroups.any {
        progress in it.probabilityRange
    }

    override fun getWordsIdsOf(
        languages: Set<LanguageCode>,
        tags: Set<Tag>,
        includeTags: Boolean,
        wordsClasses: Set<Long>,
        memorizingProbabilities: Set<MDWordsListMemorizingProbabilityGroup>,
    ): Flow<Set<Long>> {
        val includeLanguages = languages.isNotEmpty()
        val effectiveIncludeTags = tags.isNotEmpty()
        val includeWordsClasses = wordsClasses.isNotEmpty()
        val includeMemorizingProbabilities = memorizingProbabilities.count() in (1..<MDWordsListMemorizingProbabilityGroup.entries.count())
        if (
            listOf(
                includeLanguages,
                effectiveIncludeTags,
                includeWordsClasses,
                includeMemorizingProbabilities
            ).none { it }
        ) {
            return wordDao.getAllWords().map { it.mapNotNull { it.id }.toSet() }
        }
        return wordDao.getWordsOf(
            includeLanguage = includeLanguages,
            languages = languages.map { it.code }.toSet(),
            includeWordClass = includeWordsClasses,
            wordsClasses = wordsClasses
        ).let { flow ->
            if (effectiveIncludeTags) {
                flow.flatMapConcat { entities ->
                    wordWithTagsDao.getWordsWithTagsRelations(
                        ids = entities.mapNotNull { it.id }
                    ).map { entitiesWithTags ->
                        entitiesWithTags.mapNotNull { (word, entities) ->
                            val matchTags = if (includeTags) entities.any { target ->
                                tags.any { source ->
                                    source.contains(target.asModel())
                                }
                            } else {
                                entities.none { target ->
                                    tags.any { source ->
                                        source.contains(target.asModel())
                                    }
                                }
                            }
                            word.takeIf { matchTags }
                        }
                    }
                }
            } else {
                flow
            }
        }.let { flow ->
            if (includeMemorizingProbabilities) {
                flow.map {
                    val now = Clock.System.now()
                    it.mapNotNull { word ->
                        val memorizingProbability = MDTrainDataSource.Default.memoryDecayFormula(
                            word = word.asWordModel(),
                            currentTime = now
                        )
                        val matchMemorizingProbability = checkMatchMemorizingProbabilityOf(
                            progress = memorizingProbability,
                            filterProgressGroups = memorizingProbabilities,
                        )
                        word.takeIf { matchMemorizingProbability }
                    }
                }
            } else {
                flow
            }
        }.map {
            it.mapNotNull { it.id }.toSet()
        }
    }

    override suspend fun getWord(wordId: Long): Word? {
        val word = wordWithTagsAndRelatedWordsDao.getWordWithTagsAndRelatedWordsRelation(wordId) ?: return null
        val wordClass = word.word.wordClassId?.let { wordClassId ->
            wordClassDao.getWordClass(wordClassId)
        }?.asWordClassModel()
        return word.asWordModel(wordClass)
    }

    override suspend fun getWord(
        language: LanguageCode,
        meaning: String,
        translation: String,
    ): Word? = wordDao.getWord(
        meaning = language.code,
        translation = meaning,
        languageCode = translation
    )?.id?.let { id ->
        getWord(id)
    }

    override fun getWordsOfIds(ids: Set<Long>): Flow<Sequence<Word>> = wordWithTagsAndRelatedWordsDao
        .getWordsWithTagsAndRelatedWordsRelations(
            ids = ids
        ).map { list ->
            val wordsClasses = wordClassDao.getAllWordClasses().first().associate {
                it.wordClass.id to it.asWordClassModel()
            }
            list.asSequence().map { entity ->
                entity.asWordModel(wordsClasses[entity.word.wordClassId])
            }
        }

    override fun getPaginatedWordsList(
        code: LanguageCode,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        searchQuery: String,
        sortBy: MDWordsListViewPreferencesSortBy,
        sortByOrder: MDWordsListSortByOrder,
    ): Flow<PagingData<Word>> = pagingDataOf(
        mapper = { wordWithRelation ->
            val wordClass = wordWithRelation.word.wordClassId?.let { wordClassId ->
                wordClassDao.getWordClass(wordClassId)
            }?.asWordClassModel()
            wordWithRelation.asWordModel(wordClass)
        }
    ) {
        when (sortBy) {
            MDWordsListViewPreferencesSortBy.Meaning -> when (sortByOrder) {
                MDWordsListSortByOrder.Asc -> wordsPaginatedDao.getPaginatedWordsAscOfMeaning(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )

                MDWordsListSortByOrder.Desc -> wordsPaginatedDao.getPaginatedWordsDescOfMeaning(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )
            }

            MDWordsListViewPreferencesSortBy.Translation -> when (sortByOrder) {
                MDWordsListSortByOrder.Asc -> wordsPaginatedDao.getPaginatedWordsAscOfTranslation(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )

                MDWordsListSortByOrder.Desc -> wordsPaginatedDao.getPaginatedWordsDescOfTranslation(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )
            }

            MDWordsListViewPreferencesSortBy.CreatedAt -> when (sortByOrder) {
                MDWordsListSortByOrder.Asc -> wordsPaginatedDao.getPaginatedWordsAscOfCreatedAt(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )

                MDWordsListSortByOrder.Desc -> wordsPaginatedDao.getPaginatedWordsDescOfCreatedAt(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )
            }

            MDWordsListViewPreferencesSortBy.UpdatedAt -> when (sortByOrder) {
                MDWordsListSortByOrder.Asc -> wordsPaginatedDao.getPaginatedWordsAscOfUpdatedAt(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )

                MDWordsListSortByOrder.Desc -> wordsPaginatedDao.getPaginatedWordsDescOfUpdatedAt(
                    languageCode = code.code,
                    targetWords = targetWords,
                    includeMeaning = includeMeaning,
                    includeTranslation = includeTranslation,
                    queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                )
            }
        }
    }

    override suspend fun deleteWords(ids: Collection<Long>) {
        wordDao.deleteWords(ids)
    }

    override suspend fun appendTagsToWords(
        wordsIds: Collection<Long>,
        tags: Collection<Tag>,
    ) {
        val tagsIds = tags.map {
            it.id.nullIfNegative() ?: tagDao.insertTag(it.asEntity())
        }
        val relations = wordsIds.map { w ->
            tagsIds.map { t ->
                WordCrossTagEntity(
                    id = null,
                    wordId = w,
                    tagId = t
                )
            }
        }.flatten()
        wordsCrossTagDao.insertWordCrossTagsList(relations)
    }

    override suspend fun saveOrUpdateWord(word: Word): Long {
        val isValidId = word.id != INVALID_ID

        if (isValidId) {
            updateWordWithRelations(word)
        } else {
            return insertWordWithRelations(word)
        }

        return word.id // Assuming the ID is populated after update/insert
    }

    private suspend fun updateWordWithRelations(word: Word) {
        db.withTransaction {
            wordDao.updateWordWithRelations(word = word.asWordEntity(), relatedWords = word.asRelatedWords())
            val relations = getWordCrossTagRelationFromTags(word.tags, word.id)
            wordsCrossTagDao.deleteWordsCrossTagsOfWord(word.id)
            wordsCrossTagDao.insertWordCrossTagsList(relations)
        }
    }

    private suspend fun insertWordWithRelations(word: Word): Long {
        return db.withTransaction {
            languageDao.insertLanguage(LanguageEntity(word.language.code))
            val wordId = wordDao.insertWordWithRelations(
                word = word.asWordEntity(),
                relatedWords = word.asRelatedWords()
            )
            val relations = getWordCrossTagRelationFromTags(word.tags, wordId)
            wordsCrossTagDao.insertWordCrossTagsList(relations)
            wordId
        }
    }

    private suspend fun getWordCrossTagRelationFromTags(
        tags: Collection<Tag>,
        wordId: Long,
    ): List<WordCrossTagEntity> {
        val tagsIds = tags.map {
            it.id.nullIfNegative() ?: tagDao.insertTag(it.asEntity())
        }

        val relations = tagsIds.map { tagId ->
            WordCrossTagEntity(
                wordId = wordId,
                tagId = tagId
            )
        }
        return relations
    }
}