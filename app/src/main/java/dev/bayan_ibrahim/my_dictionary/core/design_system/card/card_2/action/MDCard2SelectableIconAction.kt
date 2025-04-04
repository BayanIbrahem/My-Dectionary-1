package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDCard2SelectableAction(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    normalTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceContainer,
    selectedTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryContainer,
    enabled: Boolean = true,
    label: String? = null,
    icon: @Composable () -> Unit,
) {
    val theme = if (selected) selectedTheme else normalTheme
    /**
     *
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    enabled: Boolean = true,
    actionLabel: String? = null,
    icon: @Composable () -> Unit,
     */
    MDCard2Action(
        onClick = onClick,
        modifier = modifier,
        theme = theme,
        enabled = enabled,
        actionLabel = label,
        icon = icon
    )
}

@Preview
@Composable
private fun MDCard2SelectableActionPreview() {
    MyDictionaryTheme {
        var selected by remember {
            mutableStateOf(false)
        }
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDCard2SelectableAction(
                    selected = selected,
                    label = "Action",
                    icon = {
                        MDIcon(MDIconsSet.BarChart)
                    },
                    onClick = { selected = !selected }
                )
            }
        }
    }
}