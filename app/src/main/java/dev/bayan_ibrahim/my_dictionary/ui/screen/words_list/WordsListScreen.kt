package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
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
                language = uiState.language,
                selectedWordsCount = uiState.selectedWords.count(),
                visibleWordsCount = uiState.words.count(),
                totalWordsCount = uiState.words.count(), // TODO, pass total words count,
                onAdjustFilterPreferences = {
                    // show filter dialog
                },
                onSelectLanguagePage = uiActions::onShowLanguageWordSpacesDialog,
                onDeleteWordSpace = {
                    // show delete confirm
                },
                onClearSelection = uiActions::onClearSelection,
                onSelectAll = uiActions::onSelectAll,
                onInvertSelection = uiActions::onInvertSelection,
                onDeleteSelection = {
                    // show delete confirm
                }
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
            onSelectLanguage = uiActions::onSelectLanguageWordSpace
        )

    }
}