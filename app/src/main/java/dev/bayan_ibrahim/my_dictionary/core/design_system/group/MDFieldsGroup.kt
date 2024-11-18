package dev.bayan_ibrahim.my_dictionary.core.design_system.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCard
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

data object MDFieldsGroupDefaults {
    val shape: CornerBasedShape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.large

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        titleColor: Color = MaterialTheme.colorScheme.onSurface,
    ) = MDFieldsGroupColors(
        containerColor = containerColor,
        contentColor = contentColor,
        titleColor = titleColor
    )

    @Composable
    fun styles(
        titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    ) = MDFieldsGroupStyles(
        titleStyle = titleStyle
    )
}

data class MDFieldsGroupColors(
    val containerColor: Color,
    val contentColor: Color,
    val titleColor: Color,
)

data class MDFieldsGroupStyles(
    val titleStyle: TextStyle,
)

@Composable
fun MDFieldsGroup(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MDFieldsGroupDefaults.shape,
    colors: MDFieldsGroupColors = MDFieldsGroupDefaults.colors(),
    styles: MDFieldsGroupStyles = MDFieldsGroupDefaults.styles(),
    title: @Composable ColumnScope.() -> Unit = {},
    fields: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides styles.titleStyle,
        ) {
            title()
        }
        MDCard(
            modifier = modifier,
            shape = shape,
            colors = MDCardDefaults.colors(
                contentContentColor = colors.contentColor,
                contentContainerColor = colors.containerColor
            ),
            headerClickable = false,
            cardClickable = false,
            headerModifier = Modifier,
            footerModifier = Modifier,
            contentModifier = Modifier,
        ) {
            Column {
                fields()
            }
        }
    }
}

@Preview
@Composable
private fun MDFieldsGroupPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDFieldsGroup(
                    title = {
                        Text("group title")
                    }
                ) {
                    MDField(
                        onClick = {},
                        enabled = false,
                        leadingIcon = {
                            Icon(Icons.Default.Face, null)
                        },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }
                    ) {
                        Text("disabled field")
                    }
                    MDField(
                        onClick = {},
                        leadingIcon = {
                            Icon(Icons.Default.Face, null)
                        },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }
                    ) {
                        Text("normal field")
                    }
                }
            }
        }
    }
}