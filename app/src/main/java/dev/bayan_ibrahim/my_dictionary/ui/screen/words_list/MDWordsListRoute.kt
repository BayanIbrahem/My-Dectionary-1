package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import MDWordsListViewPreferencesDialogRoute
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog.MDWordsListTrainPreferencesDialogRoute

@Composable
fun MDWordsListRoute(
    navArgs: MDDestination.TopLevel.WordsList,
    navigateToWordsDetails: (wordId: Long?, code: LanguageCode) -> Unit,
    navigateToTrainScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDWordsListViewModel = hiltViewModel(),
) {
    DisposableEffect(Unit) {
        viewModel.initWithNavArgs(navArgs)
        this.onDispose { }
    }

    val uiState = viewModel.uiState
    val wordsList = viewModel.paginatedWordsList.collectAsLazyPagingItems()
    val lifeMemorizingProbability by viewModel.lifeMemorizingProbability.collectAsStateWithLifecycle()
    val navActions by remember(uiState.selectedWordSpace.language.code) {
        derivedStateOf {
            object : MDWordsListNavigationUiActions {
                override fun navigateToWordDetails(wordId: Long?) = navigateToWordsDetails(wordId, uiState.selectedWordSpace.language.code)
            }
        }
    }

    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }

    MDWordsListScreen(
        uiState = uiState,
        uiActions = uiActions,
        wordsList = wordsList,
        modifier = modifier,
        lifeMemorizingProbability = lifeMemorizingProbability
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