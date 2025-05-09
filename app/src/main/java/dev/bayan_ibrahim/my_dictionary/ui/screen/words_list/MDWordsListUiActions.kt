package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDWordsListBusinessUiActions {
    // work space
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

    fun onShowTrainDialog()
    fun onDismissTrainDialog()

    fun onShowViewPreferencesDialog()
    fun onDismissViewPreferencesDialog()
    fun onConfirmAppendTagsOnSelectedWords(selectedTags: List<Tag>)
    fun onSpeakWord(word: Word)
    fun onSearchQueryChange(query: String)
}

interface MDWordsListNavigationUiActions: MDAppNavigationUiActions {
    fun navigateToWordDetails(wordId: Long?)
}

@Immutable
class MDWordsListUiActions(
    navigationActions: MDWordsListNavigationUiActions,
    businessActions: MDWordsListBusinessUiActions,
) : MDWordsListBusinessUiActions by businessActions, MDWordsListNavigationUiActions by navigationActions {
}

