package dev.bayan_ibrahim.my_dictionary.core.ui.card

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.LocalClipCardListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem

/**
 * selectable [MDCard2ListItem]
 * and have [onClick]
 * to toggle [checked] state.
 * @param onClick required action for toggling state, **PROVIDE CURRENT CHECKING STATE**
 * @param checked trigger where this is selected or not
 * @param checkedTheme default value is [theme], used if the selected value needed to be different than unselected value
 * @see MDCard2ListItem
 */
@Composable
fun MDCard2SelectableItem(
    checked: Boolean,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    checkedTheme: MDCard2ListItemTheme = theme,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: ((currentState: Boolean) -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    val checkableTheme by remember(checked, theme, checkedTheme) {
        derivedStateOf {
            if (checked) checkedTheme else theme
        }
    }
    MDCard2ListItem(
        modifier = modifier,
        onClick = onClick?.let {
            {
                it(!checked)
            }
        },
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        leading = leading,
        trailing = trailing,
        subtitle = subtitle,
        theme = checkableTheme,
        title = title,
    )
}

/**
 * selectable [MDCard2ListItem]
 * and have [onClick]
 * to toggle [checked] state.
 * @param onClick required action for toggling state, **PROVIDE CURRENT CHECKING STATE**
 * @param checked trigger where this is selected or not
 * @param checkedTheme default value is [theme], used if the selected value needed to be different than unselected value
 * @see MDCard2ListItem
 */
@Composable
fun MDCard2SelectableItem(
    checked: Boolean,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    checkedTheme: MDCard2ListItemTheme = theme,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    clip: Boolean = LocalClipCardListItem.current,
    onClick: ((currentState: Boolean) -> Unit)? = null,
    title: String,
) {
    val checkableTheme by remember(checked, theme, checkedTheme) {
        derivedStateOf {
            if (checked) checkedTheme else theme
        }
    }
    MDCard2ListItem(
        modifier = modifier,
        onClick = onClick?.let {
            {
                it(!checked)
            }
        },
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        leading = leading,
        trailing = trailing,
        subtitle = if (subtitle != null) {
            {
                Text(subtitle)
            }
        } else {
            null
        },
        theme = checkableTheme,
        clip = clip,
        title = {
            Text(title)
        },
    )
}
