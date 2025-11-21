package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorViewModel

@Composable
fun MDImportFromFileRoute(
    args: MDDestination.ImportFromFile,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    importViewModel: MDImportFromFileViewModel = hiltViewModel(),
    tagsViewModel: MDTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        importViewModel.initWithNavArgs(args)
    }

    val uiState = importViewModel.uiState
    val summary by remember {
        derivedStateOf {
            importViewModel.importSummary
        }
    }
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
    MDImportFromFileScreen(
        uiState = uiState,
        summary = summary,
        uiActions = uiActions,
        modifier = modifier,
    )
}
