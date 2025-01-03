package dev.bayan_ibrahim.my_dictionary.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordsPaginatedDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class MDRoomWordRepo(
    private val db: MDDataBase,
) : WordRepo {
    private val wordDao: WordDao = db.getWordDao()
    private val wordsPaginatedDao: WordsPaginatedDao = db.getWordsPaginatedDao()
    private val wordWithTagsDao: WordWithContextTagDao = db.getWordWithContextTagDao()
    private val wordWithContextTagsAndRelatedWordsDao: WordWithContextTagsAndRelatedWordsDao = db.getWordsWithContextTagAndRelatedWordsDao()
    private val wordsCrossTagDao: WordsCrossContextTagDao = db.getWordsCrossTagsDao()
    private val typeTagDao: WordTypeTagDao = db.getWordTypeTagDao()
    private val languageDao: LanguageDao = db.getLanguageDao()
    private val contextTagDao: ContextTagDao = db.getContextTagDao()

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
        contextTags: Set<Long>,
        typeTags: Set<Long>,
        memorizingProbabilities: Set<MDWordsListMemorizingProbabilityGroup>,
    ): Flow<Set<Long>> {
        val includeLanguages = languages.isNotEmpty()
        val includeContextTags = contextTags.isNotEmpty()
        val includeTypeTags = typeTags.isNotEmpty()
        val includeMemorizingProbabilities = memorizingProbabilities.count() in (1..<MDWordsListMemorizingProbabilityGroup.entries.count())
        if (
            listOf(
                includeLanguages,
                includeContextTags,
                includeTypeTags,
                includeMemorizingProbabilities
            ).none { it }
        ) {
            return wordDao.getAllWords().map { it.mapNotNull { it.id }.toSet() }
        }
        return wordDao.getWordsOf(
            includeLanguage = includeLanguages,
            languages = languages.map { it.code }.toSet(),
            includeTypeTag = includeTypeTags,
            typeTags = typeTags
        ).let { flow ->
            if (includeContextTags) {
                flow.flatMapConcat { entities ->
                    wordWithTagsDao.getWordsWithTagsRelations(
                        ids = entities.mapNotNull { it.id }
                    ).map { entitiesWithTags ->
                        entitiesWithTags.mapNotNull { (word, tags) ->
                            val matchTags = tags.any {
                                it.tagId in contextTags
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
        val word = wordWithContextTagsAndRelatedWordsDao.getWordWithContextTagsAndRelatedWordsRelation(wordId) ?: return null
        val typeTag = word.word.wordTypeTagId?.let { wordTypeTagId ->
            typeTagDao.getTagType(wordTypeTagId)
        }?.asTagModel()
        return word.asWordModel(typeTag)
    }

    override fun getWordsOfIds(ids: Set<Long>): Flow<Sequence<Word>> = wordWithContextTagsAndRelatedWordsDao
        .getWordsWithContextTagsAndRelatedWordsRelations(
            ids = ids
        ).map { list ->
            list.asSequence().map { entity -> entity.asWordModel() }
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
            val wordTypeTag = wordWithRelation.word.wordTypeTagId?.let { typeTagId ->
                typeTagDao.getTagType(typeTagId)
            }?.asTagModel()
            wordWithRelation.asWordModel(wordTypeTag)
        }
    ) {
        when (sortByOrder) {
            MDWordsListSortByOrder.Asc -> wordsPaginatedDao.getPaginatedWordsAscOf(
                languageCode = code.code,
                targetWords = targetWords,
                includeMeaning = includeMeaning,
                includeTranslation = includeTranslation,
                queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                sortBy = wordDao.getSortByColumnName(sortBy)
            )

            MDWordsListSortByOrder.Desc -> wordsPaginatedDao.getPaginatedWordsDescOf(
                languageCode = code.code,
                targetWords = targetWords,
                includeMeaning = includeMeaning,
                includeTranslation = includeTranslation,
                queryPattern = wordDao.getQueryPatternOfQuery(searchQuery),
                sortBy = wordDao.getSortByColumnName(sortBy)
            )
        }
    }

    override suspend fun deleteWords(ids: Collection<Long>) {
        wordDao.deleteWords(ids)
    }

    override suspend fun appendTagsToWords(
        wordsIds: Collection<Long>,
        tags: Collection<ContextTag>,
    ) {
        val tagsIds = tags.map {
            it.id.nullIfInvalid() ?: contextTagDao.insertContextTag(it.asEntity())
        }
        val relations = wordsIds.map { w ->
            tagsIds.map { t ->
                WordCrossContextTagEntity(
                    id = null,
                    wordId = w,
                    tagId = t
                )
            }
        }.flatten()
        wordsCrossTagDao.insertWordCrossContextTagsList(relations)
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
            val relations = getWordCrossContextTagRelationFromTags(word.tags, word.id)
            wordsCrossTagDao.deleteWordsCrossContextTagsOfWord(word.id)
            wordsCrossTagDao.insertWordCrossContextTagsList(relations)
        }
    }

    private suspend fun insertWordWithRelations(word: Word): Long {
        return db.withTransaction {
            languageDao.insertLanguage(LanguageEntity(word.language.code))
            val wordId = wordDao.insertWordWithRelations(
                word = word.asWordEntity(),
                relatedWords = word.asRelatedWords()
            )
            val relations = getWordCrossContextTagRelationFromTags(word.tags, wordId)
            wordsCrossTagDao.insertWordCrossContextTagsList(relations)
            wordId
        }
    }

    private suspend fun getWordCrossContextTagRelationFromTags(
        tags: Collection<ContextTag>,
        wordId: Long,
    ): List<WordCrossContextTagEntity> {
        val tagsIds = tags.map {
            it.id.nullIfInvalid() ?: contextTagDao.insertContextTag(it.asEntity())
        }

        val relations = tagsIds.map { tagId ->
            WordCrossContextTagEntity(
                wordId = wordId,
                tagId = tagId
            )
        }
        return relations
    }
}