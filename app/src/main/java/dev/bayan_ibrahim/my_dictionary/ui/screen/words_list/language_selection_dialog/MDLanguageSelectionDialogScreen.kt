package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
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
            firstCapStringResource(
                R.string.x_with_y,
                firstCapPluralsResource(R.plurals.language, it),
                firstCapStringResource(R.string.words)
            )
        },
        secondaryListCountTitleBuilder = {
            firstCapStringResource(
                R.string.x_without_y,
                firstCapPluralsResource(R.plurals.language, it),
                firstCapStringResource(R.string.words)
            )
        }
    )
}