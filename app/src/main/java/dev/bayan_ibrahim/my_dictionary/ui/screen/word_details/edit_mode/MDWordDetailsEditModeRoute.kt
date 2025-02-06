package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorViewModel

@Composable
fun MDWordDetailsEditModeRoute(
    wordDetails: MDDestination.WordDetailsEditMode,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    onNavigateToDetailsViewMode: (wordId: Long, language: LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
    wordDetailsViewModel: MDWordDetailsEditModeViewModel = hiltViewModel(),
    tagsSelectorViewModel: MDTagsSelectorViewModel = hiltViewModel(),
) {
    LaunchedEffect(wordDetails) {
        wordDetailsViewModel.initWithNavArgs(wordDetails)
        tagsSelectorViewModel.init()
    }

    val uiState = wordDetailsViewModel.uiState
    val tagsSelectorUiState = tagsSelectorViewModel.uiState

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
            object : MDTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<Tag>) {
                    uiActions.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            tagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }
    MDWordDetailsEditModeScreen(
        uiState = uiState,
        uiActions = uiActions,
        tagsState = tagsSelectorUiState,
        tagsActions = tagsSelectorUiActions,
        modifier = modifier,
    )
}
