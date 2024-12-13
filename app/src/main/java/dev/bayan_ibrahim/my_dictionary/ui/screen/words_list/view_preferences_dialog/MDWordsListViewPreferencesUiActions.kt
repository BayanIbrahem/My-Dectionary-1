package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy

interface MDWordsListViewPreferencesBusinessUiActions {
    fun onSearchQueryChange(query: String)
    fun onSelectSearchTarget(searchTarget: MDWordsListSearchTarget)
    fun onSelectTag(tag: String)
    fun onRemoveTag(tag: String)
    fun onClearViewPreferences()
    fun onTagSearchQueryChange(query: String)
    fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean)
    fun onSelectLearningGroup(group: MDWordsListLearningProgressGroup)
    fun onToggleAllLearningProgressGroups(selected: Boolean)
    fun onSelectWordsSortBy(sortBy: MDWordsListViewPreferencesSortBy)
    fun onSelectWordsSortByOrder(order: MDWordsListSortByOrder)
}

interface MDWordsListViewPreferencesNavigationUiActions {
    fun onDismissDialog()
}

@androidx.compose.runtime.Immutable
class MDWordsListViewPreferencesUiActions(
    navigationActions: MDWordsListViewPreferencesNavigationUiActions,
    businessActions: MDWordsListViewPreferencesBusinessUiActions,
) : MDWordsListViewPreferencesBusinessUiActions by businessActions, MDWordsListViewPreferencesNavigationUiActions by navigationActions