package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDStatisticsRoute(
    statistics: MDDestination.TopLevel.Statistics,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TODO, add statistics route
        Text("Statistics Screen")
    }
}