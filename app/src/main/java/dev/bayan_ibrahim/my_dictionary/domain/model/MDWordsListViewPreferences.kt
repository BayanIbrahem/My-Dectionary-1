package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryRegexNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagRegexNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy


interface MDWordsListViewPreferences {
    val searchQuery: String
    val searchTarget: MDWordsListSearchTarget
    val selectedTags: Set<String>
    val includeSelectedTags: Boolean
    val selectedLearningProgressGroups: Set<MDWordsListLearningProgressGroup>
    val sortBy: MDWordsListViewPreferencesSortBy
    val sortByOrder: MDWordsListSortByOrder

    val effectiveFilter: Boolean
        get() = searchQuery.isNotBlank()
                || selectedTags.isNotEmpty()
                || (selectedLearningProgressGroups.count() in 1..(MDWordsListLearningProgressGroup.entries.count()))

    fun matches(word: Word): Boolean = matchesSearch(word)
            && matchesTags(word)
            && matchesLearningGroup(word)

    private fun matchesSearch(word: Word): Boolean {
        if (searchQuery.isBlank()) return true
        val normalizedSearchQuery = searchQuery.searchQueryRegexNormalize.toRegex()

        val matchMeaning = searchTarget.includeMeaning && normalizedSearchQuery.matches(word.meaning.meaningSearchNormalize)
        val matchTranslation = searchTarget.includeTranslation && normalizedSearchQuery.matches(word.translation.meaningSearchNormalize)

        return matchMeaning || matchTranslation
    }

    private fun matchesTags(word: Word): Boolean {
        if (selectedTags.isEmpty()) return true
        val normalizedWordTags = word.tags.map { it.tagMatchNormalize }
        return selectedTags.any {
            it.tagRegexNormalize.toRegex().let { regex ->
                normalizedWordTags.any { tag -> regex.matches(tag) }
            }
        }
    }

    private fun matchesLearningGroup(word: Word): Boolean {
        if (selectedLearningProgressGroups.isEmpty()) return true
        if (selectedLearningProgressGroups.count() == MDWordsListLearningProgressGroup.entries.count()) return true
        return selectedLearningProgressGroups.any { group ->
            word.learningProgress in group.learningRange
        }
    }
}

data class WordsListViewPreferencesBuilder(
    override val searchQuery: String,
    override val searchTarget: MDWordsListSearchTarget,
    override val selectedTags: Set<String>,
    override val includeSelectedTags: Boolean,
    override val selectedLearningProgressGroups: Set<MDWordsListLearningProgressGroup>,
    override val sortBy: MDWordsListViewPreferencesSortBy,
    override val sortByOrder: MDWordsListSortByOrder,
) : MDWordsListViewPreferences

val defaultWordsListViewPreferences by lazy {
    WordsListViewPreferencesBuilder(
        searchQuery = INVALID_TEXT,
        searchTarget = MDWordsListSearchTarget.All,
        selectedTags = emptySet(),
        includeSelectedTags = true,
        selectedLearningProgressGroups = emptySet(),
        sortBy = MDWordsListViewPreferencesSortBy.Meaning,
        sortByOrder = MDWordsListSortByOrder.Asc,
    )
}