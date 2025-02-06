package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy

interface MDWordsListViewPreferencesBusinessUiActions {
    fun onSearchQueryChange(query: String)
    fun onSelectSearchTarget(searchTarget: MDWordsListSearchTarget)
    fun onClearViewPreferences()
    fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean)
    fun onSelectLearningGroup(group: MDWordsListMemorizingProbabilityGroup)
    fun onToggleAllMemorizingProbabilityGroups(selected: Boolean)
    fun onSelectWordsSortBy(sortBy: MDWordsListViewPreferencesSortBy)
    fun onSelectWordsSortByOrder(order: MDWordsListSortByOrder)
    fun onUpdateSelectedTags(selectedTags: List<Tag>)
}

interface MDWordsListViewPreferencesNavigationUiActions {
    fun onDismissDialog()
}

@androidx.compose.runtime.Immutable
class MDWordsListViewPreferencesUiActions(
    navigationActions: MDWordsListViewPreferencesNavigationUiActions,
    businessActions: MDWordsListViewPreferencesBusinessUiActions,
) : MDWordsListViewPreferencesBusinessUiActions by businessActions, MDWordsListViewPreferencesNavigationUiActions by navigationActions