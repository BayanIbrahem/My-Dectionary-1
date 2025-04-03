package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


/**
 * animate content when the [flagContent] is null
 */
@Composable
fun MDAnimatedContent(
    flagContent: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    label: String = "AnimatedVisibility",
    getContent: (@Composable (content: @Composable () -> Unit) -> Unit) = {
        it()
    },
) {
    val visible by remember(flagContent != null) {
        derivedStateOf { flagContent != null }
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label
    ) {
        flagContent?.let {
            getContent(it)
        }
    }
}