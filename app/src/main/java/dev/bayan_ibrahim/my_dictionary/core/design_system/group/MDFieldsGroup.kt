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
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCard
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

data object MDFieldsGroupDefaults {
    val shape: CornerBasedShape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.large

    @Composable
    fun colors(
        fieldColors: MDFieldColors = MDFieldDefaults.colors(),
        titleColor: Color = MaterialTheme.colorScheme.onSurface,
    ) = MDFieldsGroupColors(
        fieldColors = fieldColors,
        titleColor = titleColor
    )

    @Composable
    fun styles(
        fieldStyles: MDFieldStyles = MDFieldDefaults.styles(),
        titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    ) = MDFieldsGroupStyles(
        fieldStyles = fieldStyles,
        titleStyle = titleStyle
    )
}

data class MDFieldsGroupColors(
    val fieldColors: MDFieldColors,
    val titleColor: Color,
)

data class MDFieldsGroupStyles(
    val fieldStyles: MDFieldStyles,
    val titleStyle: TextStyle,
)

data class MDFieldsGroupTheme(
    val colors: MDFieldsGroupColors,
    val style: MDFieldsGroupStyles,

    )

val LocalMDFieldsGroupFieldTheme: ProvidableCompositionLocal<MDFieldsGroupTheme?> = compositionLocalOf(structuralEqualityPolicy()) {
    null
}

@Composable
fun MDFieldsGroup(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MDFieldsGroupDefaults.shape,
    colors: MDFieldsGroupColors = MDFieldsGroupDefaults.colors(),
    styles: MDFieldsGroupStyles = MDFieldsGroupDefaults.styles(),
    title: @Composable ColumnScope.() -> Unit = {},
    fields: @Composable ColumnScope.() -> Unit,
) {
    val theme by remember {
        derivedStateOf {
            MDFieldsGroupTheme(colors, styles)
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides styles.titleStyle,
        ) {
            title()
        }
        MDCard(
            shape = shape,
            headerClickable = false,
            cardClickable = false,
            headerModifier = Modifier,
            footerModifier = Modifier,
            contentModifier = Modifier,
        ) {
            CompositionLocalProvider(
                LocalMDFieldsGroupFieldTheme provides theme
            ) {
                Column {
                    fields()
                }
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