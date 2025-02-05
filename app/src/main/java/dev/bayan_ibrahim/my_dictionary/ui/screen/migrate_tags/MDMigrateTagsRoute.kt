package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
fun MDMigrateTagsRoute(
    args: MDDestination,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    viewModel: MDMigrateTagsViewModel = hiltViewModel(),
    contextTagsSelectorViewModel: MDContextTagsSelectorViewModel = hiltViewModel(),
) {
    DisposableEffect(args) {
        contextTagsSelectorViewModel.init()
        viewModel.initWithNavArgs(args)
        onDispose {  }
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDMigrateTagsNavigationUiActions, MDAppNavigationUiActions by appActions {
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }

    val tagsSelectorUiState = contextTagsSelectorViewModel.uiState

    val tagsSelectorNavActions by remember {
        derivedStateOf {
            object : MDContextTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<ContextTag>) {
                    super.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            contextTagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }
    MDMigrateTagsScreen(
        uiState = uiState,
        uiActions = uiActions,
        tagsSelectorUiState = tagsSelectorUiState,
        tagsSelectorUiActions = tagsSelectorUiActions,
        modifier = modifier,
    )
}
