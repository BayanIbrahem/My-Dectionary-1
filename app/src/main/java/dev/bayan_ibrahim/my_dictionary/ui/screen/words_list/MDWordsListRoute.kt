package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDWordsListRoute(
    navArgs: MDDestination.TopLevel.WordsList,
    navigateToWordsDetails: (wordId: Long?, code: LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDWordsListViewModel = hiltViewModel(),
) {
    LaunchedEffect(navArgs) {
        viewModel.initWithNavArgs(navArgs)
    }

    val uiState = viewModel.uiState
    val wordsList by viewModel.wordsList.collectAsStateWithLifecycle()
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
    )
}