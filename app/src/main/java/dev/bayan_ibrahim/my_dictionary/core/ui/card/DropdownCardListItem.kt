package dev.bayan_ibrahim.my_dictionary.core.ui.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDropdown
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.LocalClipCardListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDDropdownListItem(
    dropdownContent: @Composable ColumnScope.() -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    clip: Boolean = LocalClipCardListItem.current,
) {
    Box {
        var showDropdown by remember(enabled) {
            mutableStateOf(false)
        }
        MDCard2ListItem(
            modifier = modifier,
            leading = leading,
            trailing = {
                val rotation by animateFloatAsState(if (showDropdown) 180f else 0f)
                MDIcon(
                    MDIconsSet.ArrowDropdown,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    },
                )
            },
            subtitle = subtitle,
            theme = theme,
            onClick = if (enabled) {
                {
                    showDropdown = true
                    onClick?.invoke()
                }
            } else null,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            clip = clip,
            title = title
        )
        MDDropdown(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
            content = dropdownContent
        )
    }
}