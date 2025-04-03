package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAnimatedContent
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2Defaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * action in the card
 * @param label action label it should be short and it is single line
 * @param onClick click action
 * @param enabled if true then the action is enabled
 * @param leading leading icon max size of it is 18
 * @param trailing trailing icon max size of it is 18
 * @param weightedLabel if true then the label would take weight at the horizontal direction,
 * and if there is no max width the label would take maximum width
 * @param theme theme of the action, some fields are used only:
 * * [MDCard2ListItemTheme.containerColor]
 * * [MDCard2ListItemTheme.titleStyle]
 * * [MDCard2ListItemTheme.titleColor]
 * * [MDCard2ListItemTheme.leadingColor]
 * * [MDCard2ListItemTheme.trailingColor]
 */
@Composable
fun MDCard2Action(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    weightedLabel: Boolean = false,
    enabled: Boolean = true,
) {
    val opacity by animateFloatAsState(if (enabled) 1f else 0.38f)
    val endPadding by remember(leading != null, trailing != null) {
        derivedStateOf {
            if (leading == null && trailing == null) 12.dp else 6.dp
        }
    }
    val containerColor by animateColorAsState(theme.containerColor)
    Row(
        modifier = modifier
            .height(32.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .drawBehind {
                drawRect(containerColor)
            }
            .padding(start = 12.dp, end = endPadding)
            .graphicsLayer { this.alpha = opacity },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MDAnimatedContent(
            flagContent = leading,
            enter = MDCard2Defaults.iconEnterAnimation,
            exit = MDCard2Defaults.iconExitAnimation
        ) { leading ->
            Box(
                modifier = Modifier.size(18.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(
                    value = LocalContentColor provides theme.leadingColor,
                    content = leading
                )
            }
        }
        val weightModifier by remember(weightedLabel) {
            derivedStateOf {
                if (weightedLabel) Modifier.weight(1f) else Modifier
            }
        }
        val titleColor by animateColorAsState(theme.titleColor)
        Text(
            modifier = weightModifier,
            text = label,
            maxLines = 1,
            style = theme.titleStyle,
            textAlign = TextAlign.Center,
            color = titleColor
        )
        MDAnimatedContent(
            flagContent = trailing,
            enter = MDCard2Defaults.iconEnterAnimation,
            exit = MDCard2Defaults.iconExitAnimation
        ) { trailing ->
            Box(
                modifier = Modifier.size(18.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides theme.trailingColor,
                    content = trailing
                )
            }
        }
    }
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
                MDCard2Action(
                    label = "Leading",
                    onClick = {
                        showLeading = !showLeading
                    },
                    leading = if (showLeading) {
                        { Icon(Icons.Filled.Add, contentDescription = "Add") }
                    } else null,
                    theme = theme
                )
                MDCard2Action(
                    label = "Trailing",
                    onClick = {},
                    trailing = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                    theme = theme
                )
                MDCard2Action(
                    label = "Both",
                    onClick = {},
                    leading = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                    trailing = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                    theme = theme
                )
                MDCard2Action(
                    label = "Disabled",
                    onClick = {},
                    enabled = false,
                    theme = theme
                )
                MDCard2Action(
                    label = "Disabled Leading",
                    onClick = {},
                    enabled = false,
                    leading = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                    theme = theme
                )
                MDCard2Action(
                    label = "Disabled Trailing",
                    onClick = {},
                    enabled = false,
                    trailing = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                    theme = theme
                )
                MDCard2Action(
                    label = "Disabled Both",
                    onClick = {},
                    enabled = false,
                    leading = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                    trailing = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                    theme = theme
                )
            }
        }
    }
}
