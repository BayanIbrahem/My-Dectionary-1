package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.Language

interface WordsListBusinessUiActions {
    // work space
    fun onLanguageWordSpaceSearchQueryChange(searchQuery: String)
    fun onShowLanguageWordSpacesDialog()
    fun onHideLanguageWordSpacesDialog()
    fun onSelectLanguageWordSpace(language: Language)

    // main clicks
    fun onClickWord(id: Long)
    fun onLongClickWord(id: Long)
    fun onAddNewWord()

    /// view
    fun onClearViewPreferences()

    // view-filters
    fun onSearchQueryChange(query: String)
    fun onToggleTagFilter(tag: String, select: Boolean)
    fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean) // include or exclude selected tags
    fun onToggleLearningProgressGroup(group: WordsListLearningProgressGroup, selected: Boolean)
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

interface WordsListNavigationUiActions {
    fun navigateToWordDetails(wordId: Long?)
}

@Immutable
class WordsListUiActions(
    navigationActions: WordsListNavigationUiActions,
    businessActions: WordsListBusinessUiActions,
) : WordsListBusinessUiActions by businessActions, WordsListNavigationUiActions by navigationActions

