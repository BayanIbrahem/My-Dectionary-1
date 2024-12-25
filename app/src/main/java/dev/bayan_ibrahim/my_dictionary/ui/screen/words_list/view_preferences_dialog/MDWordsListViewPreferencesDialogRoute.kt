import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesViewModel

@Composable
fun MDWordsListViewPreferencesDialogRoute(
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDWordsListViewPreferencesViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.initWithNavArgs()
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDWordsListViewPreferencesNavigationUiActions {
                override fun onDismissDialog() {
                    onDismissDialog()
                }
            }
        }
    }
    val contextTagsSelectionState = viewModel.contextTagsState
    val contextTagsSelectionActions = viewModel.contextTagsActions
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDWordsListViewPreferencesDialog(
        showDialog = showDialog,
        uiState = uiState,
        uiActions = uiActions,
        contextTagsSelectionState = contextTagsSelectionState,
        contextTagsSelectionActions = contextTagsSelectionActions,
        modifier = modifier,
    )
}
