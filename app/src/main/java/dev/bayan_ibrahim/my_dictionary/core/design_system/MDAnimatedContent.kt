package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * animate content when the [flagContent] is null
 */
@Composable
fun MDAnimatedContent(
    flagContent: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    label: String = "MDAnimatedContent",
    getContent: (@Composable (content: @Composable () -> Unit) -> Unit) = {
        it()
    },
) {
    AnimatedContent(
        flagContent,
        modifier = modifier,
        label = label
    ) {
        if (it != null) {
            getContent(it)
        }
    }
}