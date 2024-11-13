package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder

interface MDWordsListBusinessUiActions {
    // work space
    fun onLanguageWordSpaceSearchQueryChange(searchQuery: String)
    fun onShowLanguageWordSpacesDialog()
    fun onHideLanguageWordSpacesDialog()
    fun onSelectLanguageWordSpace(wordSpace: LanguageWordSpace)
    fun onDeleteLanguageWordSpace()
    fun onConfirmDeleteLanguageWordSpace()
    fun onCancelDeleteLanguageWordSpace()

    // main clicks
    fun onClickWord(id: Long)
    fun onLongClickWord(id: Long)
    fun onAddNewWord()

    /// view
    fun onClearViewPreferences()

    // view-filters
    fun onHideViewPreferencesDialog()
    fun onShowViewPreferencesDialog()

    fun onSearchQueryChange(query: String)
    fun onSelectSearchTarget(searchTarget: WordsListSearchTarget)
    fun onTagSearchQueryChange(query: String)
    fun onSelectTag(tag: String)
    fun onRemoveTag(tag: String)

    fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean) // include or exclude selected tags
    fun onSelectLearningGroup(group: WordsListLearningProgressGroup)
    fun onToggleAllLearningProgressGroups(selected: Boolean)

    // view-orders
    fun onSelectWordsSortBy(sortBy: WordsListSortBy)
    fun onSelectWordsSortByOrder(order: WordsListSortByOrder)

    // menu-actions
    fun onSelectAll()
    fun onDeselectAll()
    fun onInvertSelection()
    fun onDeleteSelection()
    fun onConfirmDeleteSelection()
    fun onCancelDeleteSelection()
    fun onClearSelection()
}

interface MDWordsListNavigationUiActions {
    fun navigateToWordDetails(wordId: Long?)
}

@Immutable
class MDWordsListUiActions(
    navigationActions: MDWordsListNavigationUiActions,
    businessActions: MDWordsListBusinessUiActions,
) : MDWordsListBusinessUiActions by businessActions, MDWordsListNavigationUiActions by navigationActions

