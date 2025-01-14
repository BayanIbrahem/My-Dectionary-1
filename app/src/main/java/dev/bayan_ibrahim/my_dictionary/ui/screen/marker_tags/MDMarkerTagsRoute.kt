package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.isMarkerTag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorViewModel

@Composable
fun MDMarkerTagsRoute(
    args: MDDestination.MarkerTags,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    markerTagsViewModel: MDMarkerTagsViewModel = hiltViewModel(),
    contextTagsSelectorViewModel: MDContextTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        markerTagsViewModel.initWithNavArgs(args)
        contextTagsSelectorViewModel.init(
            allowedFilter = {
                !it.isMarkerTag
            },
            selectedTagsMaxSize = 1,
        )
    }

    val uiState = markerTagsViewModel.uiState
    val markerTags by markerTagsViewModel.markerTags.collectAsStateWithLifecycle()
    val tagsSelectorUiState = contextTagsSelectorViewModel.uiState

    val navActions by remember {
        derivedStateOf {
            object : MDMarkerTagsNavigationUiActions, MDAppNavigationUiActions by appActions {
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            markerTagsViewModel.getUiActions(navActions)
        }
    }
    val tagsSelectorNavActions by remember {
        derivedStateOf {
            object : MDContextTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<ContextTag>) {
                    uiActions.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            contextTagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }
    MDMarkerTagsScreen(
        uiState = uiState,
        markerTags = markerTags,
        nonMarkerTagsState = tagsSelectorUiState,
        nonMarkerTagsActions = tagsSelectorUiActions,
        uiActions = uiActions,
        modifier = modifier,
    )
}
