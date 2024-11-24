package dev.bayan_ibrahim.my_dictionary.data

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
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListViewPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MDWordsListRepoImpl(
    private val wordDao: WordDao,
    private val tagDao: WordTypeTagDao,
    private val wordSpaceDao: LanguageWordSpaceDao,
    private val preferences: MDPreferences,
) : MDWordsListRepo {
    override fun getViewPreferences(): Flow<WordsListViewPreferences> = preferences.getWordsListViewPreferencesStream()

    override suspend fun setViewPreferences(preferences: WordsListViewPreferences) = this.preferences.writeWordsListViewPreferences {
        preferences
    }

    override suspend fun setTrainPreferences(preferences: WordsListTrainPreferences) = this.preferences.writeWordsListTrainPreferences {
        preferences
    }

    override suspend fun setSelectedLanguagePage(code: LanguageCode) {
        preferences.writeUserPreferences {
            it.copy(selectedLanguagePage = code.language)
        }
    }

    override suspend fun getSelectedLanguagePage(): Language? = getSelectedLanguagePageStream().firstOrNull()

    override fun getSelectedLanguagePageStream(): Flow<Language?> {
        return preferences.getUserPreferencesStream().map {
            it.selectedLanguagePage ?: wordSpaceDao.getLanguagesWordSpaces().first().firstOrNull()?.let { workSpace ->
                workSpace.languageCode.code.language
            }
        }
    }

    override fun getLanguageTags(code: LanguageCode): Flow<Set<String>> = wordDao.getTagsInLanguage(code.code).map {
        it.map {
            StringListConverter.stringToListConverter(it)
        }.flatten().toSet()
    }

    override fun getWordsList(
        code: LanguageCode,
        viewPreferences: WordsListViewPreferences,
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

    private fun WordWithRelatedWords.checkMatchViewPreferences(preferences: WordsListViewPreferences): Boolean {
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
        val wordLearningGroup = WordsListLearningProgressGroup.of(this.word.learningProgress)

        val matchLearningGroup = wordLearningGroup in preferences.selectedLearningProgressGroups || preferences.selectedLearningProgressGroups.isEmpty()
        @Suppress("RedundantIf", "RedundantSuppression")
        if (!matchLearningGroup) return false

        return true
    }

    private fun WordEntity.searchQueryOf(searchTarget: WordsListSearchTarget): Sequence<String> = sequence {
        if (searchTarget.includeMeaning) {
            yield(this@searchQueryOf.meaning)
        }
        if (searchTarget.includeTranslation) {
            yield(this@searchQueryOf.translation)
            yieldAll(this@searchQueryOf.additionalTranslations)
        }
    }

    private fun List<Word>.sort(
        sortBy: WordsListViewPreferencesSortBy,
        order: WordsListSortByOrder,
    ): List<Word> {
        return when (sortBy) {
            WordsListViewPreferencesSortBy.Meaning -> when (order) {
                WordsListSortByOrder.Asc -> sortedBy { it.meaning }
                WordsListSortByOrder.Desc -> sortedByDescending { it.meaning }
            }

            WordsListViewPreferencesSortBy.Translation -> when (order) {
                WordsListSortByOrder.Asc -> sortedBy { it.translation }
                WordsListSortByOrder.Desc -> sortedByDescending { it.translation }
            }

            WordsListViewPreferencesSortBy.LearningProgress -> when (order) {
                WordsListSortByOrder.Asc -> sortedBy { it.learningProgress }
                WordsListSortByOrder.Desc -> sortedByDescending { it.learningProgress }
            }
        }
    }
}