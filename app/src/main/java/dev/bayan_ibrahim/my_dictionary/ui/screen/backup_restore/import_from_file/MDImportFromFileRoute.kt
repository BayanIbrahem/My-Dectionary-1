package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDImportFromFileRoute(
    args: MDDestination.ImportFromFile,
    modifier: Modifier = Modifier,
    viewModel: MDImportFromFileViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        viewModel.initWithNavArgs(args)
    }

    val uiState = viewModel.uiState
    val summary by remember {
        derivedStateOf {
            viewModel.importSummary
        }
    }
    val navActions by remember {
        derivedStateOf {
            object : MDImportFromFileNavigationUiActions {
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDImportFromFileScreen(
        uiState = uiState,
        summary = summary,
        uiActions = uiActions,
        modifier = modifier,
    )
}
