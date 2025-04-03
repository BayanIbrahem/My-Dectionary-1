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
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDCard2SelectableAction(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    checkIcon: @Composable () -> Unit = {
        val tint by animateColorAsState(LocalContentColor.current)
        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = tint)
    },
    trailing: (@Composable () -> Unit)? = null,
    normalTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceContainer,
    selectedTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryContainer,
    weightedLabel: Boolean = false,
    enabled: Boolean = true,
) {
    val theme = if (selected) selectedTheme else normalTheme
    MDCard2Action(
        label = label,
        onClick = onClick,
        modifier = modifier,
        leading = if (selected) checkIcon else null,
        trailing = trailing,
        theme = theme,
        weightedLabel = weightedLabel,
        enabled = enabled
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
                    onClick = { selected = !selected }
                )
            }
        }
    }
}