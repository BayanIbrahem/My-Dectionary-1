package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListViewPreferencesSortBy
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet

class WordsListViewPreferencesMutableState(
    searchQuery: String = defaultWordsListViewPreferences.searchQuery,
    searchTarget: WordsListSearchTarget = defaultWordsListViewPreferences.searchTarget,
    selectedTags: Set<String> = defaultWordsListViewPreferences.selectedTags,
    includeSelectedTags: Boolean = defaultWordsListViewPreferences.includeSelectedTags,
    selectedLearningProgressGroups: Set<WordsListLearningProgressGroup> = defaultWordsListViewPreferences.selectedLearningProgressGroups,
    sortBy: WordsListViewPreferencesSortBy = defaultWordsListViewPreferences.sortBy,
    sortByOrder: WordsListSortByOrder = defaultWordsListViewPreferences.sortByOrder,
) : WordsListViewPreferencesState {
    constructor(data: WordsListViewPreferences) : this(
        searchQuery = data.searchQuery,
        searchTarget = data.searchTarget,
        selectedTags = data.selectedTags,
        includeSelectedTags = data.includeSelectedTags,
        selectedLearningProgressGroups = data.selectedLearningProgressGroups,
        sortBy = data.sortBy,
        sortByOrder = data.sortByOrder
    )

    override var showDialog: Boolean by mutableStateOf(false)
    override var searchQuery: String by mutableStateOf(searchQuery)
    override var searchTarget: WordsListSearchTarget by mutableStateOf(searchTarget)
    override var selectedTags: PersistentSet<String> by mutableStateOf(selectedTags.toPersistentSet())
    override var includeSelectedTags: Boolean by mutableStateOf(includeSelectedTags)
    override var selectedLearningProgressGroups: PersistentSet<WordsListLearningProgressGroup> by mutableStateOf(
        selectedLearningProgressGroups.toPersistentSet()
    )
    override var sortBy: WordsListViewPreferencesSortBy by mutableStateOf(sortBy)
    override var sortByOrder: WordsListSortByOrder by mutableStateOf(sortByOrder)

    fun onApplyPreferences(preferences: WordsListViewPreferences) {
        searchQuery = preferences.searchQuery
        searchTarget = preferences.searchTarget
        selectedTags = preferences.selectedTags.toPersistentSet()
        includeSelectedTags = preferences.includeSelectedTags
        selectedLearningProgressGroups = preferences.selectedLearningProgressGroups.toPersistentSet()
        sortBy = preferences.sortBy
        sortByOrder = preferences.sortByOrder
    }
}
