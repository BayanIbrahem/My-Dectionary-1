package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDIconDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    icon: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    menuOffset: DpOffset = DpOffset.Zero,
    menuItems: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier) {
        icon()
        MDDropdown(
            expanded = expanded,
            modifier = menuModifier,
            onDismissRequest = onDismissRequest,
            content = menuItems,
            offset = menuOffset
        )
    }
}

@Preview
@Composable
private fun MDIconDropDownMenuPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                MDIconDropdown(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    icon = {
                        IconButton(
                            onClick = {
                                expanded = true
                            }
                        ) {
                            MDIcon(MDIconsSet.Menu)
                        }
                    },
                ) {
                    repeat(3) {
                        DropdownMenuItem(
                            text = { Text("Item") },
                            onClick = {
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}