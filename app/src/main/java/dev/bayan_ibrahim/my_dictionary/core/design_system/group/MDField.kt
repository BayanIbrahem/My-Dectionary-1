package dev.bayan_ibrahim.my_dictionary.core.design_system.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

data object MDFieldDefaults {
    val horizontalDividerThickness: Dp = 1.dp
    val height: Dp = 42.dp

    @Composable
    fun colors(
        enabledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        enabledContentColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        disabledContentColor: Color = MaterialTheme.colorScheme.outlineVariant,
        dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    ) = MDFieldColors(
        enabledContainerColor = enabledContainerColor,
        enabledContentColor = enabledContentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        dividerColor = dividerColor,
    )

    @Composable
    fun styles(
        titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    ) = MDFieldStyles(
        titleStyle = titleStyle
    )
}

data class MDFieldColors(
    val enabledContainerColor: Color,
    val enabledContentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val dividerColor: Color,
)

data class MDFieldStyles(
    val titleStyle: TextStyle,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDField(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    colors: MDFieldColors = MDFieldDefaults.colors(),
    styles: MDFieldStyles = MDFieldDefaults.styles(),
    bottomHorizontalDividerThickness: Dp = MDFieldDefaults.horizontalDividerThickness,
    /**
     * pass a height in the modifier to override this value
     */
    height: Dp = MDFieldDefaults.height,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
    },
    title: @Composable RowScope.() -> Unit,
) {
    Column {
        val containerColor by remember(enabled) {
            derivedStateOf {
                if (enabled) {
                    colors.enabledContainerColor
                } else {
                    colors.disabledContainerColor
                }
            }
        }

        val contentColor by remember(enabled) {
            derivedStateOf {
                if (enabled) {
                    colors.enabledContentColor
                } else {
                    colors.disabledContentColor
                }
            }
        }
        Surface(
            modifier = modifier.height(height = height),
            color = containerColor,
            contentColor = contentColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIcon()
                CompositionLocalProvider(
                    LocalTextStyle provides styles.titleStyle,
                ) {
                    title()
                }
                Spacer(modifier = Modifier.weight(1f))
                trailingIcon()
            }
        }
        HorizontalDivider(
            modifier = Modifier.height(bottomHorizontalDividerThickness),
            color = colors.dividerColor
        )
    }
}

@Preview
@Composable
private fun MDFieldsPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDField(
                    onClick = {},
                    leadingIcon = {
                        Icon(Icons.Default.Face, null)
                    },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                ) {
                    Text("this is a field")
                }
            }
        }
    }
}