package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.util.removePadding
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

object MDDropdownDefaults {
    val cornerRadius: Dp = 16.dp
}

@Composable
fun MDDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        modifier = modifier.removePadding(vertical = 8.dp),
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(MDDropdownDefaults.cornerRadius),
        offset = offset,
        content = content,
    )
}

@Preview
@Composable
private fun MDDropdownPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                Button(onClick = {
                    expanded = true
                }) {
                    Text("Show")
                    MDDropdown(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(10.dp, 10.dp),
                    ) {
                        MDCard2ListItem(
                            title = "Option 1",
                            trailingIcon = {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                            }
                        )
                        MDCard2ListItem(title = "Option 2", trailingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) })
                        MDCard2ListItem(title = "Option 3", trailingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) })
                    }
                }

            }
        }
    }
}