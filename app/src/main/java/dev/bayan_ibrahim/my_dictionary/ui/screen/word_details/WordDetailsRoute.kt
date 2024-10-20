package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun WordDetailsRoute(
    wordDetails: MDDestination.WordDetails,
    pop: () -> Unit,
    modifier: Modifier = Modifier,
    wordsDetailsViewModel: WordDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        wordsDetailsViewModel.initWithNavigationArgs(wordDetails)
    }
    val uiState by remember {
        derivedStateOf {
            wordsDetailsViewModel.uiState
        }
    }
    val navigationUiActions by remember {
        derivedStateOf {
            object : WordDetailsNavigationUiActions {
                override fun pop() = pop()
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            WordDetailsUiActions(
                navigationActions = navigationUiActions,
                businessActions = wordsDetailsViewModel.getUiActions(navigationUiActions)
            )
        }
    }
    WordDetailsScreen(
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}