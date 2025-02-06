package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState

@Composable
fun MDWordDetailsViewModeRoute(
    wordDetails: MDDestination.WordDetailsViewMode,
    modifier: Modifier = Modifier,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    onNavigateToWordStatistics: (wordID: Long) -> Unit,
    onEdit: (Long, LanguageCode) -> Unit,
    viewModel: MDWordDetailsViewModeViewModel = hiltViewModel(),
) {
    LaunchedEffect(wordDetails) {
        viewModel.initWithNavArgs(wordDetails)
    }

    val uiState = viewModel.uiState
    val wordAlignmentSource by viewModel.wordDetailsDirectionSource.collectAsStateWithLifecycle()
    val navActions by remember {
        derivedStateOf {
            object : MDWordDetailsViewModeNavigationUiActions, MDAppNavigationUiActions by appActions {
                override fun onClickWordStatistics() {
                    onNavigateToWordStatistics(uiState.word.id)
                }

                override fun onEdit() {
                    onEdit(uiState.word.id, uiState.word.language)
                }

                override fun onShare() {
//                    TODO("Not yet implemented")
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDWordDetailsViewModeScreen(
        uiState = uiState,
        wordAlignmentSource= wordAlignmentSource,
        uiActions = uiActions,
        modifier = modifier,
    )
}
