package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.ui.MDLanguageSelectionDialog

@Composable
fun MDLanguageSelectionDialogScreen(
    showDialog: Boolean,
    uiState: MDLanguageSelectionDialogUiState,
    uiActions: MDLanguageSelectionDialogUiActions,
    modifier: Modifier = Modifier,
) {
    MDLanguageSelectionDialog(
        showDialog = showDialog,
        onDismissRequest = uiActions::onDismissRequest,
        query = uiState.query,
        onQueryChange = uiActions::onQueryChange,
        primaryList = uiState.languagesWithWords,
        secondaryList = uiState.languagesWithoutWords,
        onSelectWordSpace = uiActions::onSelectWordSpace,
        modifier = modifier,
        primaryListCountTitleBuilder = {
            "Languages with words $it" // TODO, string res,
        },
        secondaryListCountTitleBuilder = {
            "Languages without words $it" // TODO, string res
        }
    )
}