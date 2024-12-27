package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

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
fun WordDetailsRoute(
    wordDetails: MDDestination.WordDetails,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    pop: () -> Unit,
    onNavigateToWordStatistics: (Long) -> Unit,
    modifier: Modifier = Modifier,
    wordsDetailsViewModel: WordDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        wordsDetailsViewModel.initWithNavArgs(wordDetails)
    }
    val uiState by remember {
        derivedStateOf {
            wordsDetailsViewModel.uiState
        }
    }
    val contextTagsState = wordsDetailsViewModel.contextTagsState
    val contextTagsActions = wordsDetailsViewModel.contextTagsActions
    val navigationUiActions by remember {
        derivedStateOf {
            object : WordDetailsNavigationUiActions, MDAppNavigationUiActions by appActions {
                override fun pop() = pop()
                override fun navigateToWordStatistics(wordId: Long) {
                    onNavigateToWordStatistics(wordId)
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            WordDetailsUiActions(
                navigationActions = navigationUiActions,
                businessActions = wordsDetailsViewModel.getUiActions(navigationUiActions)
            )
        }
    }
    WordDetailsScreen(
        uiState = uiState,
        uiActions = uiActions,
        contextTagsState = contextTagsState,
        contextTagsActions = contextTagsActions,
        modifier = modifier,
    )
}