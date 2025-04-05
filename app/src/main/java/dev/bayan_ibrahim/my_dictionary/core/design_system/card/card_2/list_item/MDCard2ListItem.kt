package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAnimatedContent
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2Defaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * list item with [leading], [trailing], [title] and [subtitle]
 * @param theme current them of the card, default value is the provided value with [LocalMDCard2ListItemTheme]
 * @param onClick click action on the entire list item.
 * @param onLongClick click action on the entire list item.
 * @param onDoubleClick click action on the entire list item.
 * @param clip wither clip the list item or not, the default value is true, and when used in a card
 * it would provide a false value for [LocalClipCardListItem]
 * if false then the title would width would be wrap content
 * * max size of [leading] is [MDCard2ListItemDefaults.leadingSize]
 * * max size of [trailing] is [MDCard2ListItemDefaults.trailingSize]
 * * min height of the item is [MDCard2ListItemDefaults.minHeight]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDCard2ListItem(
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    clip: Boolean = LocalClipCardListItem.current,
    title: @Composable () -> Unit,
) {
    val clickableModifier by remember(onClick) {
        derivedStateOf {
            if (onClick == null && onLongClick == null && onDoubleClick == null) Modifier
            else Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongClick, onDoubleClick = onDoubleClick)
        }
    }

    val clipModifier by remember(clip) {
        derivedStateOf {
            if(clip) Modifier.clip(RoundedCornerShape(MDCard2Defaults.cornerRadius)) else Modifier
        }
    }
    val containerColor by animateColorAsState(theme.containerColor)
    Row(
        modifier = modifier
            .heightIn(min = MDCard2ListItemDefaults.minHeight)
            .then(clipModifier)
            .drawBehind {
                drawRect(containerColor)
            }
            .then(clickableModifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        MDAnimatedContent(
            flagContent = leading,
        ) { leading ->
            CompositionLocalProvider(
                LocalContentColor provides theme.leadingColor,
            ) {
                Box(
                    modifier = Modifier.sizeIn(
                        minWidth = MDCard2ListItemDefaults.leadingSize,
                        minHeight = MDCard2ListItemDefaults.leadingSize,
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    leading()
                }
            }
        }

        val startPadding by remember(leading == null) {
            derivedStateOf {
                if (leading == null) MDCard2ListItemDefaults.noLeadingStartPadding else 0.dp
            }
        }

        val endPadding by remember(trailing == null) {
            derivedStateOf {
                if (trailing == null) MDCard2ListItemDefaults.noLeadingStartPadding else 0.dp
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = startPadding, end = endPadding),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides theme.titleColor,
                LocalTextStyle provides theme.titleStyle,
                content = title,
            )
            if (subtitle != null) {
                CompositionLocalProvider(
                    LocalContentColor provides theme.subtitleColor,
                    LocalTextStyle provides theme.subtitleStyle,
                    content = subtitle,
                )
            }
        }

        MDAnimatedContent(
            flagContent = trailing,
        ) { trailing ->
            CompositionLocalProvider(
                LocalContentColor provides theme.trailingColor,
            ) {
                Box(
                    modifier = Modifier.sizeIn(
                        minWidth = MDCard2ListItemDefaults.trailingSize,
                        minHeight = MDCard2ListItemDefaults.trailingSize,
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    trailing()
                }
            }
        }
    }
}

@Preview
@Composable
private fun MDCard2ListItemPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var selected by remember {
                mutableStateOf(false)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDCard2ListItem(
                    onClick = {
                        selected = !selected
                    },
                    theme = if (selected) MDCard2ListItemTheme.Primary else MDCard2ListItemTheme.SurfaceContainer,
                    leading = {
                        Icon(Icons.Default.Call, null)
                    },

                    trailing = {
                        Icon(Icons.Default.Close, null)
                    },
                    subtitle = {
                        Text("Some subtitle")
                    }
                ) {
                    Text("Some title")
                }
            }
        }
    }
}