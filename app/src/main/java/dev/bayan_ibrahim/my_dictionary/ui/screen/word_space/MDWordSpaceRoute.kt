package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState

@Composable
fun MDWordSpaceRoute(
    navArgs: MDDestination.TopLevel.WordSpace,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    onNavigateToStatistics: (Language) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDWordSpaceViewModel = hiltViewModel(),
) {
    LaunchedEffect(navArgs) {
        viewModel.initWithNavArgs(navArgs)
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDWordSpaceNavigationUiActions, MDAppNavigationUiActions by appActions {
                override fun navigateToStatistics(language: Language) {
                    onNavigateToStatistics(language)
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDWordSpaceScreen(
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}