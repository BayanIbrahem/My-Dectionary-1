package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * card list item
 * @param title title text
 * @param titleMaxLines max lines of title, default is  [MDCard2ListItemDefaults.titleMaxLines]
 * @param subtitle subtitle text
 * @param subtitleMaxLines max lines of title, default is  [MDCard2ListItemDefaults.subtitleMaxLines]
 * @param theme current them of the card, default value is the provided value with [LocalMDCard2ListItemTheme]
 * @param onClick click action on the entire list item.
 * @param onLongClick click action on the entire list item.
 * @param onDoubleClick click action on the entire list item.
 * @param leadingIcon content icon of leading, if [onLeadingClick] is not null then the leading would be an icon button
 * size of the icon would be [MDCard2ListItemDefaults.leadingIconSize]
 * @param onLeadingClick callback of the leading icon button click, if null, then leading would be a normal icon,
 * size of icon button would be [MDCard2ListItemDefaults.leadingSize]
 * @param enableLeading enable of disable the leading icon button if [onLeadingClick] is null then this param has no effect
 * @param trailingIcon content icon of trailing, if [onTrailingClick] is not null then the trailing would be an icon button
 * size of the icon would be [MDCard2ListItemDefaults.trailingIconSize]
 * @param onTrailingClick callback of the trailing icon button click, if null, then trailing would be a normal icon,
 * size of icon button would be [MDCard2ListItemDefaults.trailingSize]
 * @param enableTrailing enable of disable the trailing icon button if [onTrailingClick] is null then this param has no effect
 *
 * * min height of the item is [MDCard2ListItemDefaults.minHeight]
 */
@Composable
fun MDCard2ListItem(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    onLeadingClick: (() -> Unit)? = null,
    enableLeading: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    enableTrailing: Boolean = true,
    onTrailingClick: (() -> Unit)? = null,
    subtitle: AnnotatedString? = null,
    titleMaxLines: Int = MDCard2ListItemDefaults.titleMaxLines,
    subtitleMaxLines: Int = MDCard2ListItemDefaults.subtitleMaxLines,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
) {
    MDCard2ListItem(
        theme = theme,
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        title = {
            val color by animateColorAsState(LocalContentColor.current)
            Text(text = title, maxLines = titleMaxLines, color = color)
        },
        subtitle = subtitle?.let {
            {
                val color by animateColorAsState(LocalContentColor.current)
                Text(text = subtitle, maxLines = subtitleMaxLines, color = color)
            }
        },
        leading = getIcon(
            icon = leadingIcon,
            onClick = onLeadingClick,
            enable = enableLeading,
            iconSize = MDCard2ListItemDefaults.leadingIconSize
        ),
        trailing = getIcon(
            icon = trailingIcon,
            onClick = onTrailingClick,
            enable = enableTrailing,
            iconSize = MDCard2ListItemDefaults.trailingIconSize
        )
    )
}

@Composable
private fun getIcon(
    icon: @Composable (() -> Unit)?,
    onClick: (() -> Unit)?,
    enable: Boolean,
    iconSize: Dp,
): (@Composable () -> Unit)? {
    return icon?.let {
        val icon = @Composable {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center
            ) { it() }
        }
        {
            if (onClick != null) {
                IconButton(
                    modifier = Modifier
                        .size(MDCard2ListItemDefaults.trailingSize),
                    onClick = onClick,
                    enabled = enable,
                    content = icon,
                )
            } else {
                icon()
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
                    leadingIcon = {
                        Icon(Icons.Default.Call, null)
                    },

                    trailingIcon = {
                        Icon(Icons.Default.Close, null)
                    },
                    subtitle = "Some subtitle",
                    title = "Some title"
                )
            }
        }
    }
}
