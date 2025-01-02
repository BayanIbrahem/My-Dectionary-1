package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import MDWordsListViewPreferencesDialogRoute
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorViewModel
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog.MDWordsListTrainPreferencesDialogRoute

@Composable
fun MDWordsListRoute(
    navArgs: MDDestination.TopLevel.WordsList,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    navigateToWordsDetails: (wordId: Long?, code: LanguageCode) -> Unit,
    navigateToTrainScreen: () -> Unit,
    modifier: Modifier = Modifier,
    wordsListViewModel: MDWordsListViewModel = hiltViewModel(),
    contextTagsSelectorViewModel: MDContextTagsSelectorViewModel = hiltViewModel(),
) {
    DisposableEffect(Unit) {
        wordsListViewModel.initWithNavArgs(navArgs)
        this.onDispose { }
    }

    val uiState = wordsListViewModel.uiState
    val wordsList = wordsListViewModel.paginatedWordsList.collectAsLazyPagingItems()
    val lifeMemorizingProbability by wordsListViewModel.lifeMemorizingProbability.collectAsStateWithLifecycle()
    val tagsSelectorUiState = contextTagsSelectorViewModel.uiState
    val selectedWordSpace by uiState.selectedWordSpace.collectAsStateWithLifecycle()

    val navActions by remember(selectedWordSpace) {
        derivedStateOf {
            object : MDWordsListNavigationUiActions, MDAppNavigationUiActions by appActions {
                override fun navigateToWordDetails(wordId: Long?) = navigateToWordsDetails(wordId, selectedWordSpace)
            }
        }
    }

    val uiActions by remember {
        derivedStateOf {
            wordsListViewModel.getUiActions(navActions)
        }
    }

    val tagsSelectorNavActions by remember {
        derivedStateOf {
            object : MDContextTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<ContextTag>) {
                    super.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            contextTagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }

    MDWordsListScreen(
        uiState = uiState,
        uiActions = uiActions,
        wordsList = wordsList,
        contextTagsSelectionState = tagsSelectorUiState,
        contextTagsSelectionActions = tagsSelectorUiActions,
        modifier = modifier,
        lifeMemorizingProbability = lifeMemorizingProbability,
    )
    // Dialog:
    // train preferences dialog:
    MDWordsListTrainPreferencesDialogRoute(
        onNavigateToTrainScreen = navigateToTrainScreen,
        onDismissDialog = uiActions::onDismissTrainDialog,
        showDialog = uiState.showTrainPreferencesDialog
    )
    MDWordsListViewPreferencesDialogRoute(
        uiState.showViewPreferencesDialog,
        onDismissDialog = uiActions::onDismissViewPreferencesDialog
    )
}