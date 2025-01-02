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
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorViewModel

@Composable
fun MDWordDetailsEditModeRoute(
    wordDetails: MDDestination.WordDetailsEditMode,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    onNavigateToDetailsViewMode: (wordId: Long, language: LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
    wordDetailsViewModel: MDWordDetailsEditModeViewModel = hiltViewModel(),
    contextTagsSelectorViewModel: MDContextTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(wordDetails) {
        wordDetailsViewModel.initWithNavArgs(wordDetails)
    }

    val uiState = wordDetailsViewModel.uiState
    val tagsSelectorUiState = contextTagsSelectorViewModel.uiState

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
            wordDetailsViewModel.getUiActions(navActions)
        }
    }

    val tagsSelectorNavActions by remember {
        derivedStateOf {
            object : MDContextTagsSelectorNavigationUiActions {}
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            contextTagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }
    MDWordDetailsEditModeScreen(
        uiState = uiState,
        uiActions = uiActions,
        contextTagsState = tagsSelectorUiState,
        contextTagsActions = tagsSelectorUiActions,
        modifier = modifier,
    )
}
