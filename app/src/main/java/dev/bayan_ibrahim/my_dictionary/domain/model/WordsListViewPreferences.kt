package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSortByOrder


interface WordsListViewPreferences {
    val searchQuery: String
    val searchTarget: WordsListSearchTarget
    val selectedTags: Set<String>
    val includeSelectedTags: Boolean
    val selectedLearningProgressGroups: Set<WordsListLearningProgressGroup>
    val sortBy: WordsListSortBy
    val sortByOrder: WordsListSortByOrder
}

data class WordsListViewPreferencesBuilder(
    override val searchQuery: String,
    override val searchTarget: WordsListSearchTarget,
    override val selectedTags: Set<String>,
    override val includeSelectedTags: Boolean,
    override val selectedLearningProgressGroups: Set<WordsListLearningProgressGroup>,
    override val sortBy: WordsListSortBy,
    override val sortByOrder: WordsListSortByOrder,
) : WordsListViewPreferences

val defaultWordsListViewPreferences by lazy {
    WordsListViewPreferencesBuilder(
        searchQuery = INVALID_TEXT,
        searchTarget = WordsListSearchTarget.All,
        selectedTags = emptySet(),
        includeSelectedTags = true,
        selectedLearningProgressGroups = emptySet(),
        sortBy = WordsListSortBy.Meaning,
        sortByOrder = WordsListSortByOrder.Asc,
    )
}