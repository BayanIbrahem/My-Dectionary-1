package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAnimatedContent
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2SelectableAction
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDCard2(
    modifier: Modifier = Modifier,
    overline: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    headerTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultHeaderTheme,
    contentTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultContentTheme,
    footerTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultFooterTheme,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        MDAnimatedContent(
            flagContent = overline,
            enter = MDCard2Defaults.itemEnterAnimation,
            exit = MDCard2Defaults.itemExitAnimation,
        )
        CompositionLocalProvider(
            value = LocalMDCard2ListItemTheme provides contentTheme
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MDCard2Defaults.cornerRadius)),
            ) {
                MDAnimatedContent(
                    flagContent = header,
                    enter = MDCard2Defaults.itemEnterAnimation,
                    exit = MDCard2Defaults.itemExitAnimation,
                ) { header ->
                    CompositionLocalProvider(
                        value = LocalMDCard2ListItemTheme provides headerTheme
                    ) {
                        header()
                    }
                }

                content()

                MDAnimatedContent(
                    modifier = modifier.fillMaxWidth(),
                    flagContent = footer,
                    enter = MDCard2Defaults.itemEnterAnimation,
                    exit = MDCard2Defaults.itemExitAnimation,
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