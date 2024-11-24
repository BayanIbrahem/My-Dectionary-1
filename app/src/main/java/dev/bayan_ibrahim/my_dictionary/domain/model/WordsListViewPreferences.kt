package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListViewPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder


interface WordsListViewPreferences {
    val searchQuery: String
    val searchTarget: WordsListSearchTarget
    val selectedTags: Set<String>
    val includeSelectedTags: Boolean
    val selectedLearningProgressGroups: Set<WordsListLearningProgressGroup>
    val sortBy: WordsListViewPreferencesSortBy
    val sortByOrder: WordsListSortByOrder

    val effectiveFilter: Boolean
        get() = searchQuery.isNotBlank()
                || selectedTags.isNotEmpty()
                || (selectedLearningProgressGroups.count() in 1..(WordsListLearningProgressGroup.entries.count()))
}

data class WordsListViewPreferencesBuilder(
    override val searchQuery: String,
    override val searchTarget: WordsListSearchTarget,
    override val selectedTags: Set<String>,
    override val includeSelectedTags: Boolean,
    override val selectedLearningProgressGroups: Set<WordsListLearningProgressGroup>,
    override val sortBy: WordsListViewPreferencesSortBy,
    override val sortByOrder: WordsListSortByOrder,
) : WordsListViewPreferences

val defaultWordsListViewPreferences by lazy {
    WordsListViewPreferencesBuilder(
        searchQuery = INVALID_TEXT,
        searchTarget = WordsListSearchTarget.All,
        selectedTags = emptySet(),
        includeSelectedTags = true,
        selectedLearningProgressGroups = emptySet(),
        sortBy = WordsListViewPreferencesSortBy.Meaning,
        sortByOrder = WordsListSortByOrder.Asc,
    )
}