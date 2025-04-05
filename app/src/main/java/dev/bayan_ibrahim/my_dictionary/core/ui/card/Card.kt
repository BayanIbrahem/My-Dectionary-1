package dev.bayan_ibrahim.my_dictionary.core.ui.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAnimatedContent
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2Defaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2SelectableAction
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.LocalClipCardListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * @param cardModifier modifier of the card without the outline
 * @param content for content param, this container provide `false` value for [LocalClipCardListItem]
 * so list item within it would not be clipped
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDCard2(
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    overline: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    headerTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultHeaderTheme,
    contentTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultContentTheme,
    footerTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultFooterTheme,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    cardColor: Color = MDCard2Defaults.cardColor,
    content: @Composable ColumnScope.() -> Unit,
) {
    val clickableModifier by remember(onClick) {
        derivedStateOf {
            if (onClick == null && onLongClick == null && onDoubleClick == null) Modifier
            else Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongClick, onDoubleClick = onDoubleClick)
        }
    }
    Column(
        modifier = modifier,
    ) {
        MDAnimatedContent(
            flagContent = overline,
        )
        CompositionLocalProvider(
            value = LocalMDCard2ListItemTheme provides contentTheme
        ) {
            Column(
                modifier = cardModifier
                    .clip(RoundedCornerShape(MDCard2Defaults.cornerRadius))
                    .drawBehind {
                        drawRect(cardColor)
                    }
                    .then(clickableModifier),
            ) {
                MDAnimatedContent(
                    flagContent = header,
                ) { header ->
                    CompositionLocalProvider(
                        LocalMDCard2ListItemTheme provides headerTheme,
                        LocalClipCardListItem provides false,
                    ) {
                        header()
                    }
                }

                CompositionLocalProvider(
                    value = LocalClipCardListItem provides false
                ) {
                    content()
                }

                MDAnimatedContent(
                    modifier = modifier.fillMaxWidth(),
                    flagContent = footer,
                ) { footer ->
                    CompositionLocalProvider(
                        value = LocalMDCard2ListItemTheme provides footerTheme
                    ) {
                        footer()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MDCard2Preview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDCard2(
                    overline = {
                        MDCard2Overline(
                            title = "Clickable title",
                            onClickTitle = {},
                        )
//                        MDCard2Overline(
//                            leading = {
//                                Icon(Icons.Default.Call, null)
//                            },
//                            trailing = {
//                                Icon(Icons.Default.Close, null)
//                            },
//                            subtitle = "some subtitle",
//                            title = "Overline with leading, trailing and subtitle"
//                        )
                    },
                    header = {
                        MDCard2ListItem(
                            title = "Header",
                            leadingIcon = {
                                Icon(Icons.Default.Call, null)
                            },

                            trailingIcon = {
                                Icon(Icons.Default.Close, null)
                            },
                            subtitle = "Some subtitle",
                        )
                    },
                    footer = {
                        MDCard2ActionRow {
                            MDCard2SelectableAction(
                                modifier = Modifier.weight(1f),
                                label = "Action 1",
                                onClick = {},
                                selected = true,
                                weightedLabel = true,
                            )
                            MDCard2SelectableAction(
                                modifier = Modifier.weight(1f),
                                label = "Action 2",
                                onClick = {},
                                selected = false,
                                weightedLabel = true,
                            )
                        }
                    }

                ) {
                    repeat(5) {
                        MDCard2ListItem(title = "Item ${it.inc()}")
                    }
                }
            }
        }
    }
}