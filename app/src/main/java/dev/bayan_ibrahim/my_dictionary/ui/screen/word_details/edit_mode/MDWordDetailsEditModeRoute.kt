package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState

@Composable
fun MDWordDetailsEditModeRoute(
    wordDetails: MDDestination.WordDetailsEditMode,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    onNavigateToDetailsViewMode: (wordId: Long, language: LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDWordDetailsEditModeViewModel = hiltViewModel(),
) {
    LaunchedEffect(wordDetails) {
        viewModel.initWithNavArgs(wordDetails)
    }

    val uiState = viewModel.uiState
    val contextTagsState = viewModel.contextTagsState
    val contextTagsActions = viewModel.contextTagsActions
    val navActions by remember {
        derivedStateOf {
            object : MDWordDetailsEditModeNavigationUiActions, MDAppNavigationUiActions by appActions {
                override fun onNavigateToViewMode(newWordId: Long, language: LanguageCode) {
                    onPop()
                    onNavigateToDetailsViewMode(newWordId, language)
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDWordDetailsEditModeScreen(
        uiState = uiState,
        uiActions = uiActions,
        contextTagsState =contextTagsState,
        contextTagsActions = contextTagsActions,
        modifier = modifier,
    )
}
