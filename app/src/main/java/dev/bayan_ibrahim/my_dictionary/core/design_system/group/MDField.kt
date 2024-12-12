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
import androidx.compose.material3.LocalContentColor
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
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    ) = MDFieldColors(
        enabledContainerColor = containerColor,
        enabledContentColor = contentColor,
        disabledContainerColor = containerColor,
        disabledContentColor = contentColor.copy(alpha = 0.38f),
        dividerColor = dividerColor,
    )

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
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    theme: MDFieldsGroupTheme? = LocalMDFieldsGroupFieldTheme.current,
    bottomHorizontalDividerThickness: Dp = MDFieldDefaults.horizontalDividerThickness,
    /**
     * pass a height in the modifier to override this value
     */
    height: Dp = MDFieldDefaults.height,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {
//        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
    },
    title: @Composable RowScope.() -> Unit,
) {
    val defaultColors = MDFieldsGroupDefaults.colors()
    val fieldColors by remember(theme) {
        derivedStateOf {
            theme?.colors?.fieldColors ?: defaultColors.fieldColors
        }
    }

    val defaultTheme = MDFieldsGroupDefaults.styles()
    val fieldStyles by remember(theme) {
        derivedStateOf {
            theme?.style?.fieldStyles ?: defaultTheme.fieldStyles
        }
    }
    val clickableModifier by remember(onClick, onLongClick) {
        derivedStateOf {
            if (onClick == null && onLongClick == null) {
                Modifier
            } else {
                Modifier.combinedClickable(
                    enabled = enabled,
                    onClick = onClick ?: {},
                    onLongClick = onLongClick
                )
            }
        }
    }
    Column {
        val containerColor by remember(enabled) {
            derivedStateOf {
                if (enabled) {
                    fieldColors.enabledContainerColor
                } else {
                    fieldColors.disabledContainerColor
                }
            }
        }

        val contentColor by remember(enabled) {
            derivedStateOf {
                if (enabled) {
                    fieldColors.enabledContentColor
                } else {
                    fieldColors.disabledContentColor
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
                    .then(clickableModifier)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIcon()
                CompositionLocalProvider(
                    LocalTextStyle provides fieldStyles.titleStyle,
                    LocalContentColor provides contentColor
                ) {
                    title()
                }
                Spacer(modifier = Modifier.weight(1f))
                trailingIcon()
            }
        }
        HorizontalDivider(
            modifier = Modifier.height(bottomHorizontalDividerThickness),
            color = fieldColors.dividerColor
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