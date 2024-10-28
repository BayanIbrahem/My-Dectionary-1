package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.Word
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordSpaceModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSortByOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class WordsListRepoImpl(
    private val wordDao: WordDao,
    private val tagDao: WordTypeTagDao,
    private val preferences: MDPreferences,
) : WordsListRepo {
    override fun getViewPreferences(): Flow<WordsListViewPreferences> = preferences.getWordsListViewPreferencesStream()

    override suspend fun setViewPreferences(preferences: WordsListViewPreferences) = this.preferences.writeWordsListViewPreferences {
        preferences
    }

    override suspend fun setSelectedLanguagePage(code: String) {
        preferences.writeUserPreferences {
            it.copy(selectedLanguagePage = allLanguages[code]!!)
        }
    }

    override suspend fun getSelectedLanguagePage(): Language? = getSelectedLanguagePageStream().firstOrNull()

    override fun getSelectedLanguagePageStream(): Flow<Language?> {
        return preferences.getUserPreferencesStream().map {
            it.selectedLanguagePage ?: wordDao.getLanguagesWordSpaces().first().firstOrNull()?.let { workSpace ->
                allLanguages[workSpace.languageCode]
            }
        }
    }

    override fun getLanguageTags(languageCode: String): Flow<Set<String>> = wordDao.getTagsInLanguage(languageCode).map {
        it.map {
            StringListConverter.stringToListConverter(it)
        }.flatten().toSet()
    }

    override fun getWordsList(
        languageCode: String,
        viewPreferences: WordsListViewPreferences,
    ): Flow<List<Word>> = wordDao.getWordsWithRelatedOfLanguage(languageCode).map {
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

    override fun getAllLanguagesWordSpaces(): Flow<List<LanguageWordSpace>> = wordDao.getLanguagesWordSpaces().map { entities ->
        entities.map(LanguageWordSpaceEntity::asWordSpaceModel)
    }

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
        }
        if (!matchTags) return false

        // learning group
        val wordLearningGroup = WordsListLearningProgressGroup.of(this.word.learningProgress)

        @Suppress("RedundantIf", "RedundantSuppression")
        if (wordLearningGroup !in preferences.selectedLearningProgressGroups) return false

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
        sortBy: WordsListSortBy,
        order: WordsListSortByOrder,
    ): List<Word> {
        return when (sortBy) {
            WordsListSortBy.Meaning -> when (order) {
                WordsListSortByOrder.Asc -> sortedBy { it.meaning }
                WordsListSortByOrder.Desc -> sortedByDescending { it.meaning }
            }

            WordsListSortBy.Translation -> when (order) {
                WordsListSortByOrder.Asc -> sortedBy { it.translation }
                WordsListSortByOrder.Desc -> sortedByDescending { it.translation }
            }

            WordsListSortBy.LearningProgress -> when (order) {
                WordsListSortByOrder.Asc -> sortedBy { it.learningProgress }
                WordsListSortByOrder.Desc -> sortedByDescending { it.learningProgress }
            }
        }
    }
}