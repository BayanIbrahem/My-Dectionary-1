package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListDeleteConfirmDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListLanguageSelectionPageDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListTopAppBar


@Composable
fun WordsListScreen(
    uiState: WordsListUiState,
    uiActions: WordsListUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        invalidDataMessage = "Select a language to start" // TODO, string res
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MDWordsListTopAppBar(
                isSelectionModeOn = uiState.isSelectModeOn,
                language = uiState.selectedWordSpace.language,
                selectedWordsCount = uiState.selectedWords.count(),
                visibleWordsCount = uiState.words.count(),
                totalWordsCount = uiState.words.count(), // TODO, pass total words count,
                onAdjustFilterPreferences = {
                    // show filter dialog
                },
                onSelectLanguagePage = uiActions::onShowLanguageWordSpacesDialog,
                onDeleteWordSpace = uiActions::onDeleteLanguageWordSpace,
                onClearSelection = uiActions::onClearSelection,
                onSelectAll = uiActions::onSelectAll,
                onInvertSelection = uiActions::onInvertSelection,
                onDeleteSelection = uiActions::onDeleteSelection
            )
        }
        //// Dialogs:
        // language page:
        MDWordsListLanguageSelectionPageDialog(
            showDialog = uiState.isLanguagesWordSpacesDialogShown,
            onDismissRequest = uiActions::onHideLanguageWordSpacesDialog,
            query = uiState.languagesWordSpaceSearchQuery,
            onQueryChange = uiActions::onLanguageWordSpaceSearchQueryChange,
            languagesWithWords = uiState.activeLanguagesWordSpaces,
            languagesWithoutWords = uiState.inactiveLanguagesWordSpaces,
            onSelectWordSpace = uiActions::onSelectLanguageWordSpace
        )
        // delete words confirm dialog:
        MDWordsListDeleteConfirmDialog(
            showDialog = uiState.isSelectedWordsDeleteDialogShown,
            isDeleteRunning = uiState.isSelectedWordsDeleteProcessRunning,
            onCancel = uiActions::onCancelDeleteSelection,
            onConfirm = uiActions::onConfirmDeleteSelection,
            title = "Delete Words", // TODO, string res
            runningDeleteMessage = "Deletion process is running please wait...",// TODO, string res
            confirmDeleteMessage = "Are you sure you want to delete ${uiState.selectedWords.count()} words?\n\n this action can not be undone."
        )
        // delete word space confirm dialog:
        MDWordsListDeleteConfirmDialog(
            showDialog = uiState.isSelectedWordsDeleteDialogShown,
            isDeleteRunning = uiState.isLanguageWordSpaceDeleteProcessRunning,
            onCancel = uiActions::onCancelDeleteLanguageWordSpace,
            onConfirm = uiActions::onConfirmDeleteLanguageWordSpace,
            title = "Delete Language", // TODO, string res
            runningDeleteMessage = "Deletion process is running please wait...",// TODO, string res
            confirmDeleteMessage = "Are you sure you want to delete ${uiState.selectedWordSpace.language.fullDisplayName} (${uiState.selectedWordSpace.wordsCount} words)?\n\n this action can not be undone."
        )
    }
}