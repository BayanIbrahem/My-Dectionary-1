package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * row for actions
 * @see MDCard2Action
 * @see MDCard2SelectableAction
 */
@Composable
fun MDCard2ActionRow(
    modifier: Modifier = Modifier,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .background(theme.containerColor),
        verticalAlignment = Alignment.CenterVertically,
        content = actions,
    )
}

@Preview
@Composable
private fun MDCard2ActionRowPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDCard2ActionRow {
                    MDCard2Action(
                        label = "Default 2",
                        onClick = {},
                        theme = MDCard2ListItemTheme.PrimaryContainer
                    )
                    MDCard2Action(
                        label = "Leading",
                        onClick = {},
                        leading = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                        theme = MDCard2ListItemTheme.ErrorContainer
                    )
                    MDCard2Action(
                        label = "Trailing",
                        onClick = {},
                        trailing = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                        theme = MDCard2ListItemTheme.PrimaryOnSurface
                    )
                    MDCard2Action(
                        label = "Both",
                        onClick = {},
                        leading = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                        trailing = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                        theme = MDCard2ListItemTheme.ErrorOnSurface
                    )
                }
            }
        }
    }
}