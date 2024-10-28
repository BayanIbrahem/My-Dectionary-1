package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

data object MDDialogDefaults {
    val primaryActionColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.primary

    val secondaryActionColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.primary

    val tertiaryActionColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.error

    val shape: CornerBasedShape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.large
}

@Composable
fun MDBasicDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MDDialogDefaults.shape,
    showActionsHorizontalDivider: Boolean = true,
    title: @Composable ColumnScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    if (showDialog)
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Card(
                modifier = modifier,
                shape = shape,
            ) {
                title()
                content()
                if (showActionsHorizontalDivider) {
                    HorizontalDivider()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    content = actions
                )
            }
        }
}

@Preview
@Composable
private fun MDBasicDialogPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MDBasicDialog(
                    showDialog = true,
                    onDismissRequest = {},
                    title = { Text("Title") },
                    actions = { Text("Actions") }
                ) {
                    Text("Body Body Body Body ")
                }
            }
        }
    }
}
