package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2Defaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2OverlineDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * there is two overline in the card
 * first one is the display value overline
 * and second is the action overline and in most cases it opens a menu that select an option
 */

/**
 * this is the general overline of the card
 * @param leading in most cases a leading icon no padding is applied between it and [title] if null
 * the title would take a padding about [MDCard2Defaults.cornerRadius] on the start.
 * default content color is [MDCard2OverlineDefaults.leadingColor]
 *
 * @param trailing like leading, no padding with [title], title would not add any end padding if this
 * value is null default content color is [MDCard2OverlineDefaults.leadingColor]
 *
 * @param subtitle optional content after the title, has a [MDCard2Defaults.cornerRadius] start padding
 * * default color is [MDCard2OverlineDefaults.subtitleColor]
 * * default style is [MDCard2OverlineDefaults.subtitleStyle]
 *
 * @param title content in most cases a normal text,
 * * has a [MDCard2Defaults.cornerRadius] start padding if [leading] is null
 * * default color is [MDCard2OverlineDefaults.titleColor]
 * * default style is [MDCard2OverlineDefaults.titleStyle]
 */

@Composable
fun MDCard2Overline(
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit),
) {
    val titleStartPadding by remember(leading != null) {
        derivedStateOf {
            if (leading == null) MDCard2Defaults.cornerRadius else 0.dp
        }
    }
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                CompositionLocalProvider(
                    LocalContentColor provides MDCard2OverlineDefaults.leadingColor,
                ) {
                    Box(
                        modifier = Modifier.sizeIn(
                            minWidth = MDCard2OverlineDefaults.leadingMinSize,
                            minHeight = MDCard2OverlineDefaults.leadingMinSize,
                        ),
                        contentAlignment = Alignment.Center,
                    ) {
                        leading()
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = titleStartPadding)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MDCard2OverlineDefaults.titleStyle,
                    LocalContentColor provides MDCard2OverlineDefaults.titleColor,
                    content = title,
                )
            }
            if (trailing != null) {
                CompositionLocalProvider(
                    LocalContentColor provides MDCard2OverlineDefaults.trailingColor,
                ) {
                    Box(
                        modifier = Modifier.sizeIn(
                            minWidth = MDCard2OverlineDefaults.trailingMinSize,
                            minHeight = MDCard2OverlineDefaults.trailingMinSize,
                        ),
                        contentAlignment = Alignment.Center,
                    ) {
                        trailing()
                    }
                }
            }
        }
        if (subtitle != null) {
            val subtitleStartPadding by remember(leading != null) {
                derivedStateOf {
                    if (leading == null) {
                        MDCard2Defaults.cornerRadius
                    } else {
                        // TODO, handle when the leading has more than min size width using a custom layout
                        MDCard2OverlineDefaults.leadingMinSize
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(start = subtitleStartPadding)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MDCard2OverlineDefaults.subtitleStyle,
                    LocalContentColor provides MDCard2OverlineDefaults.subtitleColor,
                    content = subtitle
                )
            }
        }
    }
}

@Preview
@Composable
private fun MDCard2OverlinePreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MDCard2Overline(
                    leading = {
                        Icon(Icons.Default.Call, null)
                    },
                    trailing = {
                        Icon(Icons.Default.Close, null)
                    },
                    subtitle = {
                        Text("some subtitle")
                    }
                ) {
                    Text("Overline with leading, trailing and subtitle")
                }
                MDCard2Overline(
                    leading = null,
                    trailing = {
                        Icon(Icons.Default.Close, null)
                    },
                ) {
                    Text("Overline with trailing and subtitle")
                }
                MDCard2Overline(
                    subtitle = {
                        Text("some subtitle")
                    }
                ) {
                    Text("Overline with subtitle")
                }
                MDCard2Overline() {
                    Text("Overline")
                }
            }
        }
    }
}