package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
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
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

data object MDHorizontalCardGroupDefaults {
    val shape: CornerBasedShape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.large

    @Composable
    fun colors(
        fieldColors: MDHorizontalCardColors = MDHorizontalCardDefaults.colors(),
        titleColor: Color = MaterialTheme.colorScheme.onSurface,
        subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ) = MDHorizontalCardGroupColors(
        cardColors = fieldColors,
        titleColor = titleColor,
        subtitleColor = subtitleColor,
    )

    @Composable
    fun primaryColors(
        titleColor: Color = MaterialTheme.colorScheme.onSurface,
        subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ) = MDHorizontalCardGroupColors(
        cardColors = MDHorizontalCardDefaults.primaryColors,
        titleColor = titleColor,
        subtitleColor = subtitleColor
    )

    @Composable
    fun styles(
        fieldStyles: MDHorizontalCardStyles = MDHorizontalCardDefaults.styles(),
        titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
        subtitleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    ) = MDHorizontalCardGroupStyles(
        cardStyles = fieldStyles,
        titleStyle = titleStyle,
        subtitleStyle = subtitleStyle
    )
}

data class MDHorizontalCardGroupColors(
    val cardColors: MDHorizontalCardColors,
    val titleColor: Color,
    val subtitleColor: Color,
)

data class MDHorizontalCardGroupStyles(
    val cardStyles: MDHorizontalCardStyles,
    val titleStyle: TextStyle,
    val subtitleStyle: TextStyle,
)

@Composable
fun MDHorizontalCardGroup(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(4.dp),
    shape: CornerBasedShape = MDHorizontalCardGroupDefaults.shape,
    colors: MDHorizontalCardGroupColors = MDHorizontalCardGroupDefaults.colors(),
    styles: MDHorizontalCardGroupStyles = MDHorizontalCardGroupDefaults.styles(),
    bottomHorizontalDividerThickness: Dp = 1.dp,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    content: MDHorizontalCardScope.() -> Unit,
) {
    val scope by rememberHorizontalCardStateOfItems(content)

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        title?.let {
            CompositionLocalProvider(
                LocalTextStyle provides styles.titleStyle,
                LocalContentColor provides colors.titleColor,
                content = title,
            )
        }
        subtitle?.let {
            CompositionLocalProvider(
                LocalTextStyle provides styles.subtitleStyle,
                LocalContentColor provides colors.subtitleColor,
                content = subtitle,
            )
        }

        MDVerticalCard(
            shape = shape,
            headerClickable = false,
            cardClickable = false,
            headerModifier = Modifier,
            footerModifier = Modifier,
            contentModifier = Modifier,
        ) {
            Column {
                val lastIndex = scope.itemsCount.dec()
                scope.items.forEachIndexed { i, item ->
                    val dividerThickness by remember(i, lastIndex) {
                        derivedStateOf {
                            if (i == lastIndex) {
                                0.dp
                            } else {
                                bottomHorizontalDividerThickness
                            }
                        }
                    }
                    MDHorizontalCard(
                        colors = item.colors ?: colors.cardColors,
                        styles = item.styles ?: styles.cardStyles,
                        bottomHorizontalDividerThickness = dividerThickness,
                        onClick = item.onClick,
                        modifier = item.modifier,
                        enabled = item.enabled,
                        onLongClick = item.onLongClick,
                        leadingIcon = item.leadingIcon,
                        trailingIcon = item.trailingIcon,
                        subtitle = item.subtitle,
                        title = item.title
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MDHorizontalCardGroupPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDHorizontalCardGroup {
                    item(
                        leadingIcon = { MDIcon(MDIconsSet.Check) },
                        trailingIcon = { MDIcon(MDIconsSet.Check) },
                        enabled = true,
                        subtitle = {
                            Text("subtitle")
                        }
                    ) {
                        Text("title")
                    }
                    item(
                        leadingIcon = { MDIcon(MDIconsSet.Check) },
                        trailingIcon = { MDIcon(MDIconsSet.Check) },
                        enabled = false,
                        subtitle = {
                            Text("subtitle")
                        }
                    ) {
                        Text("title")
                    }
                    item(
                        leadingIcon = {},
                        trailingIcon = { MDIcon(MDIconsSet.Check) },
                        enabled = true,
                        subtitle = {
                            Text("subtitle")
                        }
                    ) {
                        Text("title")
                    }
                    item(
                        leadingIcon = { MDIcon(MDIconsSet.Check) },
                        trailingIcon = {},
                        enabled = true,
                        subtitle = {
                            Text("subtitle")
                        }
                    ) {
                        Text("title")
                    }

                    item(
                        leadingIcon = { MDIcon(MDIconsSet.Check) },
                        trailingIcon = { MDIcon(MDIconsSet.Check) },
                        enabled = true,
                    ) {
                        Text("title")
                    }
                }
            }
        }
    }
}