package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences

import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListViewPreferencesSortBy

interface WordsListViewPreferencesActions {
    fun onSearchQueryChange(query: String)
    fun onSelectSearchTarget(searchTarget: WordsListSearchTarget)
    fun onSelectTag(tag: String)
    fun onRemoveTag(tag: String)
    fun onClearViewPreferences()
    fun onHideViewPreferencesDialog()
    fun onShowViewPreferencesDialog()
    fun onTagSearchQueryChange(query: String)
    fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean)
    fun onSelectLearningGroup(group: WordsListLearningProgressGroup)
    fun onToggleAllLearningProgressGroups(selected: Boolean)
    fun onSelectWordsSortBy(sortBy: WordsListViewPreferencesSortBy)
    fun onSelectWordsSortByOrder(order: WordsListSortByOrder)
}