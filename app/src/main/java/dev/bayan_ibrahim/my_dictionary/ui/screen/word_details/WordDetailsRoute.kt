package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun WordDetailsRoute(
    wordDetails: MDDestination.WordDetails,
    modifier: Modifier = Modifier,
    wordsDetailsViewModel: WordDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        wordsDetailsViewModel.initWithNavigationArgs(wordDetails)
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TODO, add words details route
        Text("Words details Screen")
    }
}
