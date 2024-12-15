package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDProfileRoute(
    profile: MDDestination.TopLevel.Profile,
    navigateToScreen: (MDDestination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDProfileViewModel = hiltViewModel(),
) {
    LaunchedEffect(profile) {
        viewModel.initWithNavArgs(profile)
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDProfileNavigationUiActions {
                override fun navigateToImportFromFile() {
                    navigateToScreen(MDDestination.ImportFromFile)
                }

                override fun navigateToExportToFile() {
                    navigateToScreen(MDDestination.ExportToFile)
                }

                override fun navigateToSync() {
                    navigateToScreen(MDDestination.Sync)
                }

                override fun navigateToAppTheme() {
                    navigateToScreen(MDDestination.AppTheme)
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDProfileScreen(
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}