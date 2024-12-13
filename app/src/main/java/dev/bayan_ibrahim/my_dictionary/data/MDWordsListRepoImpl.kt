package dev.bayan_ibrahim.my_dictionary.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordSpaceModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


class MDWordsListRepoImpl(
    private val wordDao: WordDao,
    private val tagDao: WordTypeTagDao,
    private val wordSpaceDao: LanguageWordSpaceDao,
    private val preferences: MDPreferencesDataStore,
) : MDWordsListRepo, MDTrainPreferencesRepo by MDTrainPreferencesRepoImpl(wordDao) {
    override fun getViewPreferences(): Flow<MDWordsListViewPreferences> = preferences.getWordsListViewPreferencesStream()

    override suspend fun setSelectedLanguagePage(code: LanguageCode) {
        preferences.writeUserPreferences {
            it.copy(selectedLanguagePage = code.language)
        }
    }

    override suspend fun getSelectedLanguagePage(): Language? = getSelectedLanguagePageStream().firstOrNull()

    override fun getSelectedLanguagePageStream(): Flow<Language?> = preferences.getUserPreferencesStream().map {
        it.selectedLanguagePage ?: wordSpaceDao.getLanguagesWordSpaces().first().firstOrNull()?.languageCode?.code?.language
    }

    override fun getLanguageTags(code: LanguageCode): Flow<Set<String>> = wordDao.getTagsInLanguage(code.code).map {
        it.map {
            StringListConverter.stringToListConverter(it)
        }.flatten().toSet()
    }

    override fun getWordsList(
        code: LanguageCode,
        viewPreferences: MDWordsListViewPreferences,
    ): Flow<List<Word>> = wordDao.getWordsWithRelatedOfLanguage(code.code).map {
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
            MDWordsListSortByOrder.Asc -> wordDao.getPaginatedWordsWithRelatedAscOf(
                languageCode = code.code,
                targetWords = wordsIdsOfTagsAndProgressRange,
                includeMeaning = viewPreferences.searchTarget.includeMeaning,
                includeTranslation = viewPreferences.searchTarget.includeTranslation,
                queryPattern = wordDao.getQueryPatternOfQuery(viewPreferences.searchQuery),
                sortBy = wordDao.getSortByColumnName(viewPreferences.sortBy)
            )

            MDWordsListSortByOrder.Desc -> wordDao.getPaginatedWordsWithRelatedDescOf(
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

    override fun getAllLanguagesWordSpaces(
        includeNotUsedLanguages: Boolean,
    ): Flow<List<LanguageWordSpace>> = if (includeNotUsedLanguages) {
        wordSpaceDao.getLanguagesWordSpaces().map { entities ->
            val dbModels = entities.map(LanguageWordSpaceEntity::asWordSpaceModel)
            (dbModels + allLanguages.map { (_, language) ->
                LanguageWordSpace(language = language)
            }).distinctBy {
                it.language.code
            }
        }
    } else {
        wordSpaceDao.getLanguagesWordSpaces().map { entities ->
            entities.map(LanguageWordSpaceEntity::asWordSpaceModel)
        }
    }

    override suspend fun getLanguagesWordSpaces(code: LanguageCode): LanguageWordSpace? =
        wordSpaceDao.getLanguagesWordSpace(code.code)?.asWordSpaceModel()

    private fun WordWithRelatedWords.checkMatchViewPreferences(preferences: MDWordsListViewPreferences): Boolean {
        // search
        val normalizedSearchQuery = preferences.searchQuery.trim().lowercase()
        val matchSearch = this.word.searchQueryOf(preferences.searchTarget).any { value ->
            normalizedSearchQuery in value.trim().lowercase()
        }
        if (!matchSearch) return false

        // tags
        val matchTags = if (preferences.includeSelectedTags) {
            this.word.tags.any { it in preferences.selectedTags }
        } else {
            this.word.tags.all { it !in preferences.selectedTags }
        } || preferences.selectedTags.isEmpty()
        if (!matchTags) return false

        // learning group
        val wordLearningGroup = MDWordsListLearningProgressGroup.of(this.word.learningProgress)

        val matchLearningGroup =
            wordLearningGroup in preferences.selectedLearningProgressGroups || preferences.selectedLearningProgressGroups.isEmpty()
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

            MDWordsListViewPreferencesSortBy.LearningProgress -> when (order) {
                MDWordsListSortByOrder.Asc -> sortedBy { it.learningProgress }
                MDWordsListSortByOrder.Desc -> sortedByDescending { it.learningProgress }
            }
        }
    }
}
