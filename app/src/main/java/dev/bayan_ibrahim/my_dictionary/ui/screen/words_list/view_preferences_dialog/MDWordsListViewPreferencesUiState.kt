package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet

interface MDWordsListViewPreferencesUiState : MDWordsListViewPreferences, MDUiState {
    val tagSearchQuery: String
    val tagsSuggestions: List<String>
}

class MDWordsListViewPreferencesMutableUiState(
    searchQuery: String = defaultWordsListViewPreferences.searchQuery,
    searchTarget: MDWordsListSearchTarget = defaultWordsListViewPreferences.searchTarget,
    selectedTags: Set<String> = defaultWordsListViewPreferences.selectedTags,
    includeSelectedTags: Boolean = defaultWordsListViewPreferences.includeSelectedTags,
    selectedMemorizingProbabilityGroups: Set<MDWordsListMemorizingProbabilityGroup> = defaultWordsListViewPreferences.selectedMemorizingProbabilityGroups,
    sortBy: MDWordsListViewPreferencesSortBy = defaultWordsListViewPreferences.sortBy,
    sortByOrder: MDWordsListSortByOrder = defaultWordsListViewPreferences.sortByOrder,
) : MDWordsListViewPreferencesUiState, MDMutableUiState() {
    constructor(data: MDWordsListViewPreferences) : this(
        searchQuery = data.searchQuery,
        searchTarget = data.searchTarget,
        selectedTags = data.selectedTags,
        includeSelectedTags = data.includeSelectedTags,
        selectedMemorizingProbabilityGroups = data.selectedMemorizingProbabilityGroups,
        sortBy = data.sortBy,
        sortByOrder = data.sortByOrder
    )

    override var searchQuery: String by mutableStateOf(searchQuery)
    override var searchTarget: MDWordsListSearchTarget by mutableStateOf(searchTarget)
    override var selectedTags: PersistentSet<String> by mutableStateOf(selectedTags.toPersistentSet())
    override var includeSelectedTags: Boolean by mutableStateOf(includeSelectedTags)
    override var selectedMemorizingProbabilityGroups: PersistentSet<MDWordsListMemorizingProbabilityGroup> by mutableStateOf(
        selectedMemorizingProbabilityGroups.toPersistentSet()
    )
    override var sortBy: MDWordsListViewPreferencesSortBy by mutableStateOf(sortBy)
    override var sortByOrder: MDWordsListSortByOrder by mutableStateOf(sortByOrder)

    override var tagSearchQuery: String by mutableStateOf(INVALID_TEXT)
    override val tagsSuggestions: SnapshotStateList<String> = mutableStateListOf()

    fun onApplyPreferences(preferences: MDWordsListViewPreferences) {
        searchQuery = preferences.searchQuery
        searchTarget = preferences.searchTarget
        selectedTags = preferences.selectedTags.toPersistentSet()
        includeSelectedTags = preferences.includeSelectedTags
        selectedMemorizingProbabilityGroups = preferences.selectedMemorizingProbabilityGroups.toPersistentSet()
        sortBy = preferences.sortBy
        sortByOrder = preferences.sortByOrder
    }
}

