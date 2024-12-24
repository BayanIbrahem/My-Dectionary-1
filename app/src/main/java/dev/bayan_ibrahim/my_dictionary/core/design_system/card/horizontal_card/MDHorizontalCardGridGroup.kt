package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerBasedShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDHorizontalCardGridGroup(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(4.dp),
    shape: CornerBasedShape = MDHorizontalCardGroupDefaults.shape,
    colors: MDHorizontalCardGroupColors = MDHorizontalCardGroupDefaults.colors(),
    styles: MDHorizontalCardGroupStyles = MDHorizontalCardGroupDefaults.styles(),
    columns: GridCells = GridCells.Fixed(1),
    lastItemSpan: (LazyGridItemSpanScope.(currentIndex: Int) -> GridItemSpan) = {
        GridItemSpan(this.maxCurrentLineSpan)
    },
    dividerThickness: Dp = 1.dp,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    content: MDHorizontalCardGridScope.() -> Unit,
) {
    val scope by rememberHorizontalCardGridStateOfItems(content)

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        title?.let {
            CompositionLocalProvider(
                LocalTextStyle provides styles.titleStyle,
                title,
            )
        }
        subtitle?.let {
            CompositionLocalProvider(
                LocalTextStyle provides styles.subtitleStyle,
                subtitle,
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
            LazyVerticalGrid(
                columns = columns,
            ) {
                val lastIndex = scope.itemsCount.dec()
                scope.items.forEachIndexed { i, item ->
                    item(
                        span = {
                            if (i == lastIndex) {
                                lastItemSpan(i)
                            } else if (item.span != null) {
                                with(item) {
                                    span!!.let { this@item.it(i) }
                                }
                            } else {
                                GridItemSpan(1)
                            }
                        }
                    ) {
                        val itemColors by remember(item.colors, colors.cardColors) {
                            derivedStateOf {
                                item.colors ?: colors.cardColors
                            }
                        }

                        val itemStyles by remember(item.styles, styles.cardStyles) {
                            derivedStateOf {
                                item.styles ?: styles.cardStyles
                            }
                        }
                        MDHorizontalCard(
                            colors = itemColors,
                            styles = itemStyles,
                            bottomHorizontalDividerThickness = 0.dp,
                            onClick = item.onClick,
                            modifier = item.modifier
                                .drawBehind {
                                    // TODO, require better drawing now to draw the line twice or draw it on the borders of the grid card
                                    drawRect(
                                        color = (item.colors ?: colors.cardColors).dividerColor,
                                        style = Stroke(dividerThickness.toPx())
                                    )
                                },
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
}

@Preview
@Composable
private fun MDHorizontalCardGridGroupPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDHorizontalCardGridGroup(
                    columns = GridCells.Adaptive(150.dp),
                    dividerThickness = 1.dp
                ) {
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
