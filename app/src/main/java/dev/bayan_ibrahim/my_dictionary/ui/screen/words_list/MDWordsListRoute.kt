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
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorViewModel
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog.MDWordsListTrainPreferencesDialogRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog.MDWordsListViewPreferencesViewModel

@Composable
fun MDWordsListRoute(
    navArgs: MDDestination.TopLevel.WordsList,
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
    navigateToWordsDetails: (wordId: Long?, code: LanguageCode) -> Unit,
    navigateToTrainScreen: () -> Unit,
    modifier: Modifier = Modifier,
    wordsListViewModel: MDWordsListViewModel = hiltViewModel(),
    tagsSelectorViewModel: MDTagsSelectorViewModel = hiltViewModel(),
) {
    DisposableEffect(Unit) {
        wordsListViewModel.initWithNavArgs(navArgs)
        tagsSelectorViewModel.init()
        this.onDispose { }
    }

    val uiState = wordsListViewModel.uiState
    val wordsList = wordsListViewModel.paginatedWordsList.collectAsLazyPagingItems()
    val lifeMemorizingProbability by wordsListViewModel.lifeMemorizingProbability.collectAsStateWithLifecycle()
    val tagsSelectorUiState = tagsSelectorViewModel.uiState
    val selectedWordSpace by uiState.selectedWordSpace.collectAsStateWithLifecycle()
    val currentSpeakingWordId by wordsListViewModel.currentSpeakingWordId.collectAsStateWithLifecycle()

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
            object : MDTagsSelectorNavigationUiActions {
                override fun onUpdateSelectedTags(selectedTags: SnapshotStateList<Tag>) {
                    super.onUpdateSelectedTags(selectedTags)
                }
            }
        }
    }
    val tagsSelectorUiActions by remember {
        derivedStateOf {
            tagsSelectorViewModel.getUiActions(tagsSelectorNavActions)
        }
    }

    val searchQuery by wordsListViewModel.searchQueryFlow.collectAsStateWithLifecycle()
    MDWordsListScreen(
        uiState = uiState,
        uiActions = uiActions,
        wordsList = wordsList,
        tagsSelectionState = tagsSelectorUiState,
        tagsSelectionActions = tagsSelectorUiActions,
        currentSpeakingWordId =currentSpeakingWordId,
        modifier = modifier,
        lifeMemorizingProbability = lifeMemorizingProbability,
        searchQuery = searchQuery,
    )
    // Dialog:
    // train preferences dialog:
    MDWordsListTrainPreferencesDialogRoute(
        onNavigateToTrainScreen = navigateToTrainScreen,
        onDismissDialog = uiActions::onDismissTrainDialog,
        showDialog = uiState.showTrainPreferencesDialog
    )
    MDWordsListViewPreferencesDialogRoute(
        showDialog = uiState.showViewPreferencesDialog,
        onDismissDialog = uiActions::onDismissViewPreferencesDialog,
    )
}