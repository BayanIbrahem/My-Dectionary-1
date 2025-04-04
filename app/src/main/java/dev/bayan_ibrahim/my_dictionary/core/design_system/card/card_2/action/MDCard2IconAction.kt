package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAnimatedContent
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme


/**
 * icon action
 * @param icon action icon
 * @param label action label nullable shown under the icon
 * @param onClick click action
 * @param enabled if true then the action is enabled
 * and if there is no max width the label would take maximum width
 * @param theme theme of the action, some fields are used only:
 * * [MDCard2ListItemTheme.containerColor]
 * * [MDCard2ListItemTheme.subtitleStyle]
 * * [MDCard2ListItemTheme.subtitleColor]
 */
@Composable
fun MDCard2Action(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    icon: @Composable () -> Unit,
) {
    val opacity by animateFloatAsState(if (enabled) 1f else 0.38f)
    val containerColor by animateColorAsState(theme.containerColor)
    Column(
        modifier = modifier
            .height(32.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .drawBehind {
                drawRect(containerColor)
            }
            .padding(start = 12.dp, end = 12.dp)
            .graphicsLayer { this.alpha = opacity },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        MDAnimatedContent(
            flagContent = label,
        ) { label ->
            CompositionLocalProvider(
                LocalContentColor provides theme.subtitleColor,
                LocalTextStyle provides theme.subtitleStyle,
                content = label
            )
        }
    }
}

/**
 * icon action
 * @param icon action icon
 * @param actionLabel action label nullable shown under the icon
 * @param onClick click action
 * @param enabled if true then the action is enabled
 * and if there is no max width the label would take maximum width
 * @param theme theme of the action, some fields are used only:
 * * [MDCard2ListItemTheme.containerColor]
 * * [MDCard2ListItemTheme.subtitleStyle]
 * * [MDCard2ListItemTheme.subtitleColor]
 */
@Composable
fun MDCard2Action(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    enabled: Boolean = true,
    actionLabel: String? = null,
    icon: @Composable () -> Unit,
) {
    MDCard2Action(
        onClick = onClick,
        modifier = modifier,
        theme = theme,
        enabled = enabled,
        label = if (actionLabel != null) {
            {
                Text(actionLabel)
            }
        } else null,
        icon = icon
    )

}

@Preview
@Composable
private fun MDCard2ActionPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val theme = MDCard2ListItemTheme.PrimaryContainer
            var showLeading by remember {
                mutableStateOf(false)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MDCard2Action(
                    label = "Default",
                    onClick = {},
                    theme = theme
                )
            }
        }
    }
}
