package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

data object MDDialogDefaults {
    @Composable
    fun colors(
        cardColors: MDCardColors = MDCardDefaults.colors(),
        primaryActionColor: Color = MaterialTheme.colorScheme.primary,
        secondaryActionColor: Color = MaterialTheme.colorScheme.primary,
        tertiaryActionColor: Color = MaterialTheme.colorScheme.primary,
    ): MDDialogColors = MDDialogColors(
        cardColors = cardColors,
        primaryActionColor = primaryActionColor,
        secondaryActionColor = secondaryActionColor,
        tertiaryActionColor = tertiaryActionColor
    )

    val shape: CornerBasedShape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.large
}

@Immutable
data class MDDialogColors(
    val cardColors: MDCardColors,
    val primaryActionColor: Color,
    val secondaryActionColor: Color,
    val tertiaryActionColor: Color,
)

@Composable
fun MDBasicDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MDDialogDefaults.shape,
    colors: MDDialogColors = MDDialogDefaults.colors(),
    textStyle: MDCardTextStyle = MDCardDefaults.textStyles(),
    headerModifier: Modifier = MDCardDefaults.headerModifier,
    footerModifier: Modifier = MDCardDefaults.footerModifier,
    contentModifier: Modifier = MDCardDefaults.contentModifier,
    showActionsHorizontalDivider: Boolean = true,
    title: @Composable BoxScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    if (showDialog)
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            MDCard(
                modifier = modifier,
                shape = shape,
                colors = colors.cardColors,
                textStyle = textStyle,
                headerModifier = headerModifier,
                footerModifier = footerModifier,
                contentModifier = contentModifier,
                headerClickable = false,
                cardClickable = false,
                header = title,
                footer = {
                    if (showActionsHorizontalDivider) {
                        HorizontalDivider()
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        content = actions
                    )
                },
                content = content,
            )
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
