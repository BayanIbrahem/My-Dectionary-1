import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorViewModel
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesViewModel

@Composable
fun MDWordsListViewPreferencesDialogRoute(
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewPreferencesViewModel: MDWordsListViewPreferencesViewModel = hiltViewModel(),
    tagsSelectorViewModel: MDTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewPreferencesViewModel.initWithNavArgs()
        tagsSelectorViewModel.init()
    }

    val uiState = viewPreferencesViewModel.uiState
    val tagsSelectorUiState = tagsSelectorViewModel.uiState

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
            object : MDTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<Tag>) {
                    super.onUpdateSelectedTags(selectedTags)
                    uiActions.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            tagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }
    MDWordsListViewPreferencesDialog(
        showDialog = showDialog,
        uiState = uiState,
        uiActions = uiActions,
        tagsSelectionState = tagsSelectorUiState,
        tagsSelectionActions = tagsSelectorUiActions,
        modifier = modifier,
    )
}
