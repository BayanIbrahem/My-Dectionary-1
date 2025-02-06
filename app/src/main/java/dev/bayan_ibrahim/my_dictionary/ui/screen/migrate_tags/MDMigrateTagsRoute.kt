package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
fun MDMigrateTagsRoute(
    args: MDDestination,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    viewModel: MDMigrateTagsViewModel = hiltViewModel(),
    tagsSelectorViewModel: MDTagsSelectorViewModel = hiltViewModel(),
) {
    DisposableEffect(args) {
        tagsSelectorViewModel.init()
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

    val tagsSelectorUiState = tagsSelectorViewModel.uiState

    val tagsSelectorNavActions by remember {
        derivedStateOf {
            object : MDTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<Tag>) {
                    super.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            tagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
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
