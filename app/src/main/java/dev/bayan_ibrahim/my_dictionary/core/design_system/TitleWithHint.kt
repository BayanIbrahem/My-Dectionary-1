package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.ui.MDPlainTooltip

/**
 * if the content is not null then it would be sounded with a hint
 */
@Composable
fun ContentWithHint(
    modifier: Modifier = Modifier,
    hint: String? = null,
    content: @Composable () -> Unit,
) {
    if (hint == null) {
        content()
    } else {
        MDPlainTooltip(
            modifier = modifier,
            tooltipContent = {
                Text(hint)
            },
            content = content,
        )
    }
}


