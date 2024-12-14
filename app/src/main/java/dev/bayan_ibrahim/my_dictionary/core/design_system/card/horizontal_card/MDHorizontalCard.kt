package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.ReadOnlyComposable
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
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme

data object MDHorizontalCardDefaults {
    val combatHeight: Dp = 42.dp
    val mediumHeight: Dp = 48.dp

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        titleColor: Color = MaterialTheme.colorScheme.onSurface,
        subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        trailingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
        disabledAlpha: Float = 0.38f,
    ) = MDHorizontalCardColors(
        enabledContainerColor = containerColor,
        enabledTitleColor = titleColor,
        enabledSubtitleColor = subtitleColor,
        enabledLeadingIconColor = leadingIconColor,
        enabledTrailingIconColor = trailingIconColor,
        disabledContainerColor = containerColor.copy(alpha = disabledAlpha),
        disabledTitleColor = titleColor.copy(alpha = disabledAlpha),
        disabledSubtitleColor = subtitleColor.copy(alpha = disabledAlpha),
        disabledLeadingIconColor = leadingIconColor.copy(alpha = disabledAlpha),
        disabledTrailingIconColor = trailingIconColor.copy(alpha = disabledAlpha),
        dividerColor = dividerColor,
    )


    @Composable
    fun colors(
        enabledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        enabledTitleColor: Color = MaterialTheme.colorScheme.onSurface,
        enabledSubtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        enabledLeadingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        enabledTrailingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        disabledTitleColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledSubtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledTrailingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    ) = MDHorizontalCardColors(
        enabledContainerColor = enabledContainerColor,
        enabledTitleColor = enabledTitleColor,
        enabledSubtitleColor = enabledSubtitleColor,
        enabledLeadingIconColor = enabledLeadingIconColor,
        enabledTrailingIconColor = enabledTrailingIconColor,
        disabledContainerColor = disabledContainerColor,
        disabledTitleColor = disabledTitleColor,
        disabledSubtitleColor = disabledSubtitleColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        dividerColor = dividerColor,
    )

    val primaryColors: MDHorizontalCardColors
        @Composable
        @ReadOnlyComposable
        get() = MDHorizontalCardColors(
            enabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            enabledTitleColor = MaterialTheme.colorScheme.onPrimaryContainer,
            enabledSubtitleColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
            enabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            enabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledTitleColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledSubtitleColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            dividerColor = MaterialTheme.colorScheme.outlineVariant,
        )

    @Composable
    fun styles(
        titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
        subtitleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    ) = MDHorizontalCardStyles(
        titleStyle = titleStyle,
        subtitleStyle = subtitleStyle
    )
}

data class MDHorizontalCardColors(
    val enabledContainerColor: Color,
    val enabledTitleColor: Color,
    val enabledSubtitleColor: Color,
    val enabledLeadingIconColor: Color,
    val enabledTrailingIconColor: Color,
    val disabledContainerColor: Color,
    val disabledTitleColor: Color,
    val disabledSubtitleColor: Color,
    val disabledLeadingIconColor: Color,
    val disabledTrailingIconColor: Color,
    val dividerColor: Color,
)

data class MDHorizontalCardStyles(
    val titleStyle: TextStyle,
    val subtitleStyle: TextStyle,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDHorizontalCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    colors: MDHorizontalCardColors = MDHorizontalCardDefaults.colors(),
    styles: MDHorizontalCardStyles = MDHorizontalCardDefaults.styles(),
    bottomHorizontalDividerThickness: Dp = 0.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) {
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
                    colors.enabledContainerColor
                } else {
                    colors.disabledContainerColor
                }
            }
        }

        val titleColor by remember(enabled) {
            derivedStateOf {
                if (enabled) {
                    colors.enabledTitleColor
                } else {
                    colors.disabledTitleColor
                }
            }
        }

        Surface(
            modifier = modifier
                .heightIn(42.dp, 60.dp)
                .height(IntrinsicSize.Min),
            color = containerColor,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(clickableModifier)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIcon?.let {
                    val leadingIconColor by remember(enabled) {
                        derivedStateOf {
                            if (enabled) {
                                colors.enabledLeadingIconColor
                            } else {
                                colors.disabledLeadingIconColor
                            }
                        }
                    }

                    CompositionLocalProvider(
                        value = LocalContentColor provides leadingIconColor,
                        content = leadingIcon,
                    )
                }
                CompositionLocalProvider(
                    LocalTextStyle provides styles.titleStyle,
                    LocalContentColor provides titleColor
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        title()
                        subtitle?.let {
                            val subtitleColor by remember(enabled) {
                                derivedStateOf {
                                    if (enabled) {
                                        colors.enabledSubtitleColor
                                    } else {
                                        colors.disabledSubtitleColor
                                    }
                                }
                            }
                            CompositionLocalProvider(
                                LocalTextStyle provides styles.subtitleStyle,
                                LocalContentColor provides subtitleColor,
                                content = subtitle,
                            )
                        }
                    }
                }
                trailingIcon?.let {
                    val trailingIconColor by remember(enabled) {
                        derivedStateOf {
                            if (enabled) {
                                colors.enabledTrailingIconColor
                            } else {
                                colors.disabledTrailingIconColor
                            }
                        }
                    }
                    CompositionLocalProvider(
                        value = LocalContentColor provides trailingIconColor,
                        content = trailingIcon
                    )
                }
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
private fun MDHorizontalCardsPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDHorizontalCard(
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