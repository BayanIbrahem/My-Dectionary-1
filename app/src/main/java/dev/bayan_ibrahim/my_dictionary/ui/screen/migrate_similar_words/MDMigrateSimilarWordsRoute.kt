package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_similar_words


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState

@Composable
fun MDMigrateSimilarWordsRoute(
    args: MDDestination,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    modifier: Modifier = Modifier,
    viewModel: MDMigrateSimilarWordsViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        viewModel.initWithNavArgs(args)
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDMigrateSimilarWordsNavigationUiActions, MDAppNavigationUiActions by appActions {

            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDMigrateSimilarWordsScreen(
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}
