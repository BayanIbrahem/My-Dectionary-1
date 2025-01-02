import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorViewModel
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesViewModel

@Composable
fun MDWordsListViewPreferencesDialogRoute(
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewPreferencesViewModel: MDWordsListViewPreferencesViewModel = hiltViewModel(),
    contextTagsSelectorViewModel: MDContextTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewPreferencesViewModel.initWithNavArgs()
    }

    val uiState = viewPreferencesViewModel.uiState
    val tagsSelectorUiState = contextTagsSelectorViewModel.uiState

    val navActions by remember {
        derivedStateOf {
            object : MDWordsListViewPreferencesNavigationUiActions {
                override fun onDismissDialog() {
                    onDismissDialog()
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewPreferencesViewModel.getUiActions(navActions)
        }
    }

    val tagsSelectorNavActions by remember {
        derivedStateOf {
            object : MDContextTagsSelectorNavigationUiActions {}
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            contextTagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }
    MDWordsListViewPreferencesDialog(
        showDialog = showDialog,
        uiState = uiState,
        uiActions = uiActions,
        contextTagsSelectionState = tagsSelectorUiState,
        contextTagsSelectionActions = tagsSelectorUiActions,
        modifier = modifier,
    )
}
