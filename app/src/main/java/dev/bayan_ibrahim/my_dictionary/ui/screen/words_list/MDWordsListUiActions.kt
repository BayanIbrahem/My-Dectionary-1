package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences.WordsListTrainPreferencesActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences.WordsListViewPreferencesActions

interface MDWordsListBusinessUiActions : WordsListViewPreferencesActions, WordsListTrainPreferencesActions {
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

    // menu-actions
//    fun onSelectAll(), invalid with pagination
    fun onDeselectAll()
//    fun onInvertSelection(), invalid with pagination
    fun onDeleteSelection()
    fun onConfirmDeleteSelection()
    fun onCancelDeleteSelection()
    fun onClearSelection()
}

interface MDWordsListNavigationUiActions {
    fun navigateToWordDetails(wordId: Long?)
    fun navigateToTrainScreen()
}

@Immutable
class MDWordsListUiActions(
    navigationActions: MDWordsListNavigationUiActions,
    businessActions: MDWordsListBusinessUiActions,
) : MDWordsListBusinessUiActions by businessActions, MDWordsListNavigationUiActions by navigationActions {
}

