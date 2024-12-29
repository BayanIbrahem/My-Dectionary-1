package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState

@Composable
fun MDMarkerTagsRoute(
    args: MDDestination.MarkerTags,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    viewModel: MDMarkerTagsViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        viewModel.initWithNavArgs(args)
    }

    val uiState = viewModel.uiState
    val markerTags by viewModel.markerTags.collectAsStateWithLifecycle()
    val nonMarkerTagsState = viewModel.nonMarkerTagsExplorerState
    val nonMarkerTagsActions = viewModel.nonMarkerTagsExplorerActions

    val navActions by remember {
        derivedStateOf {
            object : MDMarkerTagsNavigationUiActions, MDAppNavigationUiActions by appActions {
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDMarkerTagsScreen(
        uiState = uiState,
        markerTags = markerTags,
        nonMarkerTagsState = nonMarkerTagsState,
        nonMarkerTagsActions = nonMarkerTagsActions,
        uiActions = uiActions,
        modifier = modifier,
    )
}
