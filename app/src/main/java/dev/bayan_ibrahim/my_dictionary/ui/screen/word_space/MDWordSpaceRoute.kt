package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDWordSpaceRoute(
    navArgs: MDDestination.TopLevel.WordSpace,
    modifier: Modifier = Modifier,
    viewModel: MDWordSpaceViewModel = hiltViewModel(),
) {
    LaunchedEffect(navArgs) {
        viewModel.initWithNavArgs(navArgs)
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDWordSpaceNavigationUiActions {
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