package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDTrainRoute(
    args: MDDestination.Train,
    modifier: Modifier = Modifier,
    navigateToStatisticsScreen: () -> Unit,
    viewModel: MDTrainViewModel = hiltViewModel(),
) {
    LaunchedEffect(args) {
        viewModel.initWithNavArgs(args)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val remainingTime by viewModel.wordRemainingTimeDataSource.collectAsStateWithLifecycle()
    val navActions by remember {
        derivedStateOf {
            object : MDTrainNavigationUiActions {
                override fun onNavigateToResultsScreen() {
                    navigateToStatisticsScreen()
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDTrainScreen(
        uiState = uiState,
        remainingTime = remainingTime,
        uiActions = uiActions,
        modifier = modifier,
    )
}
