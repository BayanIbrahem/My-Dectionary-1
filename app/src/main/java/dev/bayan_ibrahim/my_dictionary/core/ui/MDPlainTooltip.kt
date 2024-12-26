package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTooltipBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDPlainTooltip(
    tooltipContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberTooltipState()
    val scope = rememberCoroutineScope()
    MDTooltipBox(
        tooltip = {
            PlainTooltip(content = tooltipContent)
        },
        state = state,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.clickable {
                scope.launch { state.show() }
            },
        ){
            content()
        }
    }
}
