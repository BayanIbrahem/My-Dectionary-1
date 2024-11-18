package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDExportToFileRoute(
    args: MDDestination.ExportToFile,
    modifier: Modifier = Modifier,
    viewModel: MDExportToFileViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        viewModel.initWithNavArgs(args)
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDExportToFileNavigationUiActions {
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDExportToFileScreen(
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}
