package dev.bayan_ibrahim.my_dictionary.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordsPaginatedDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithContextTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModelSet
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDLanguageSelectionDialogRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map


class MDWordsListRepoImpl(
    private val wordDao: WordDao,
    private val wordsPaginatedDao: WordsPaginatedDao,
    private val wordWithTagsDao: WordWithContextTagDao,
    private val wordWithTagAndRelatedWordsDao: WordWithContextTagsAndRelatedWordsDao,
    private val tagDao: WordTypeTagDao,
    private val preferences: MDPreferencesDataStore,
    languageRepo: MDLanguageSelectionDialogRepo,
) : MDWordsListRepo, MDTrainPreferencesRepo by MDTrainPreferencesRepoImpl(wordWithTagsDao), MDLanguageSelectionDialogRepo by languageRepo {
    override fun getViewPreferences(): Flow<MDWordsListViewPreferences> = preferences.getWordsListViewPreferencesStream()
    override fun getUserPreferences(): Flow<MDUserPreferences> = preferences.getUserPreferencesStream()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLanguageTags(code: LanguageCode): Flow<Set<ContextTag>> = wordDao
        .getWordsIdsOfLanguage(code.code).flatMapConcat { wordsIds ->
            wordWithTagsDao.getWordsWithTagsRelations(wordsIds)
        }.map {
            it.map {
                it.tags.map { it.asModel() }
            }.flatten().toSet()
        }

    override fun getWordsList(
        code: LanguageCode,
        viewPreferences: MDWordsListViewPreferences,
    ): Flow<List<Word>> = wordWithTagAndRelatedWordsDao.getWordsWithContextTagsAndRelatedWordsRelations(code.code).map {
        it.mapNotNull { wordWithRelation ->
            if (wordWithRelation.checkMatchViewPreferences(viewPreferences)) {
                val wordTypeTag = wordWithRelation.word.wordTypeTagId?.let { typeTagId ->
                    tagDao.getTagType(typeTagId)
                }?.asTagModel()
                wordWithRelation.asWordModel(wordTypeTag)
            } else {
                null
            }
        }.sort(sortBy = viewPreferences.sortBy, order = viewPreferences.sortByOrder)
    }

    override fun getPaginatedWordsList(
        code: LanguageCode,
        wordsIdsOfTagsAndProgressRange: Set<Long>,
        viewPreferences: MDWordsListViewPreferences,
    ): Flow<PagingData<Word>> = pagingDataOf(
        mapper = { wordWithRelation ->
            val wordTypeTag = wordWithRelation.word.wordTypeTagId?.let { typeTagId ->
                tagDao.getTagType(typeTagId)
            }?.asTagModel()
            wordWithRelation.asWordModel(wordTypeTag)
        }
    ) {
        when (viewPreferences.sortByOrder) {
            MDWordsListSortByOrder.Asc -> wordsPaginatedDao.getPaginatedWordsAscOf(
                languageCode = code.code,
                targetWords = wordsIdsOfTagsAndProgressRange,
                includeMeaning = viewPreferences.searchTarget.includeMeaning,
                includeTranslation = viewPreferences.searchTarget.includeTranslation,
                queryPattern = wordDao.getQueryPatternOfQuery(viewPreferences.searchQuery),
                sortBy = wordDao.getSortByColumnName(viewPreferences.sortBy)
            )

            MDWordsListSortByOrder.Desc -> wordsPaginatedDao.getPaginatedWordsDescOf(
                languageCode = code.code,
                targetWords = wordsIdsOfTagsAndProgressRange,
                includeMeaning = viewPreferences.searchTarget.includeMeaning,
                includeTranslation = viewPreferences.searchTarget.includeTranslation,
                queryPattern = wordDao.getQueryPatternOfQuery(viewPreferences.searchQuery),
                sortBy = wordDao.getSortByColumnName(viewPreferences.sortBy)
            )
        }
    }

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


    override suspend fun deleteWords(ids: Collection<Long>) {
        wordDao.deleteWords(ids)
    }

    private fun WordWithContextTagsAndRelatedWordsRelation.checkMatchViewPreferences(preferences: MDWordsListViewPreferences): Boolean {
        // search
        val normalizedSearchQuery = preferences.searchQuery.trim().lowercase()
        val matchSearch = this.word.searchQueryOf(preferences.searchTarget).any { value ->
            normalizedSearchQuery in value.trim().lowercase()
        }
        if (!matchSearch) return false

        val wordTags = this.tags.asModelSet()

        // tags
        val matchTags = preferences.matchesTags(wordTags)
        if (!matchTags) return false

        // learning group
        val wordLearningGroup = MDWordsListMemorizingProbabilityGroup.of(this.word.memoryDecayFactor)

        val matchLearningGroup =
            wordLearningGroup in preferences.selectedMemorizingProbabilityGroups || preferences.selectedMemorizingProbabilityGroups.isEmpty()
        @Suppress("RedundantIf", "RedundantSuppression")
        if (!matchLearningGroup) return false

        return true
    }


    private fun WordEntity.searchQueryOf(searchTarget: MDWordsListSearchTarget): Sequence<String> = sequence {
        if (searchTarget.includeMeaning) {
            yield(this@searchQueryOf.meaning)
        }
        if (searchTarget.includeTranslation) {
            yield(this@searchQueryOf.translation)
            yieldAll(this@searchQueryOf.additionalTranslations)
        }
    }

    private fun List<Word>.sort(
        sortBy: MDWordsListViewPreferencesSortBy,
        order: MDWordsListSortByOrder,
    ): List<Word> {
        return when (sortBy) {
            MDWordsListViewPreferencesSortBy.Meaning -> when (order) {
                MDWordsListSortByOrder.Asc -> sortedBy { it.meaning }
                MDWordsListSortByOrder.Desc -> sortedByDescending { it.meaning }
            }

            MDWordsListViewPreferencesSortBy.Translation -> when (order) {
                MDWordsListSortByOrder.Asc -> sortedBy { it.translation }
                MDWordsListSortByOrder.Desc -> sortedByDescending { it.translation }
            }

            MDWordsListViewPreferencesSortBy.MemorizingProbability -> when (order) {
                MDWordsListSortByOrder.Asc -> sortedBy { it.memoryDecayFactor }
                MDWordsListSortByOrder.Desc -> sortedByDescending { it.memoryDecayFactor }
            }
        }
    }
}
