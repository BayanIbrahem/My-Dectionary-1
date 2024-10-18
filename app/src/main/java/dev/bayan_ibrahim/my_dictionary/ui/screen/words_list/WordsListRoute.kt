package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun WordsListRoute(
    wordsList: MDDestination.TopLevel.WordsList,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TODO, add word list route
        Text("Words list Screen")
    }
}