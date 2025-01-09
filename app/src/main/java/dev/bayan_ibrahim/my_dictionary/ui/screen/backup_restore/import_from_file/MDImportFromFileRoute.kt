package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorViewModel

@Composable
fun MDImportFromFileRoute(
    args: MDDestination.ImportFromFile,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    importViewModel: MDImportFromFileViewModel = hiltViewModel(),
    contextTagsViewModel: MDContextTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        importViewModel.initWithNavArgs(args)
        contextTagsViewModel.init()
    }

    val uiState = importViewModel.uiState
    val summary by remember {
        derivedStateOf {
            importViewModel.importSummary
        }
    }
    val tagsSelectorUiState = contextTagsViewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDImportFromFileNavigationUiActions, MDAppNavigationUiActions by appActions{
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            importViewModel.getUiActions(navActions)
        }
    }
    val tagsSelectorNavigationActions by remember {
        derivedStateOf {
            object : MDContextTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<ContextTag>) {
                    super.onUpdateSelectedTags(selectedTags)
                    importViewModel.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            contextTagsViewModel.getUiActions(tagsSelectorNavigationActions)
        }
    }
    MDImportFromFileScreen(
        uiState = uiState,
        summary = summary,
        uiActions = uiActions,
        modifier = modifier,
        tagsSelectorUiState = tagsSelectorUiState,
        tagsSelectorUiActions = tagsSelectorUiActions

    )
}
