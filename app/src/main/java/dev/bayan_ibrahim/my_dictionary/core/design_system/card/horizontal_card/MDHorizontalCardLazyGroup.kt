package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.bottomOnly
import dev.bayan_ibrahim.my_dictionary.ui.theme.topOnly

/**
 * should not have a vertical spacing cause the items in the group should has 0 vertical spacing between them,
 */
fun LazyListScope.horizontalCardGroup(
    itemsCount: Int,
    shape: CornerBasedShape,
    topOnlyShape: CornerBasedShape,
    bottomOnlyShape: CornerBasedShape,
    middleShape: CornerBasedShape,
    colors: MDHorizontalCardGroupColors,
    styles: MDHorizontalCardGroupStyles,
    bottomHorizontalDividerThickness: Dp = 1.dp,
    /**
     * append vertical spacing as padding in the title not in the lazy column, cause [content] should not have content spaced by
     */
    title: (@Composable () -> Unit)? = null,
    /**
     * append vertical spacing as padding in the title not in the lazy column, cause [content] should not have content spaced by
     */
    subtitle: (@Composable () -> Unit)? = null,
    content: @Composable (i: Int) -> Unit,
) {
    title?.let {
        item {
            CompositionLocalProvider(
                LocalTextStyle provides styles.titleStyle,
                LocalContentColor provides colors.titleColor
            ) {
                title()
            }
        }
    }
    subtitle?.let {
        item {
            CompositionLocalProvider(
                LocalTextStyle provides styles.subtitleStyle,
                LocalContentColor provides colors.subtitleColor
            ) {
                subtitle()
            }
        }
    }
    items(itemsCount) { i ->
        val isLast by remember(i) {
            derivedStateOf {
                i == itemsCount.dec()
            }
        }
        val itemShape by remember(i) {
            derivedStateOf {
                if (itemsCount == 1) {
                    shape
                } else if (i == 0) {
                    topOnlyShape
                } else if (isLast) {
                    bottomOnlyShape
                } else {
                    middleShape
                }
            }
        }
        val dividerThickness by remember(isLast) {
            derivedStateOf {
                if (isLast) {
                    0.dp
                } else {
                    bottomHorizontalDividerThickness
                }
            }
        }
        Column(
            modifier = Modifier.clip(itemShape)
        ) {
            content(i)
            HorizontalDivider(thickness = dividerThickness)
        }
    }
}

@Composable
fun MDHorizontalCardLazyGroup(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MDHorizontalCardGroupDefaults.shape,
    colors: MDHorizontalCardGroupColors = MDHorizontalCardGroupDefaults.colors(),
    styles: MDHorizontalCardGroupStyles = MDHorizontalCardGroupDefaults.styles(),
    bottomHorizontalDividerThickness: Dp = 1.dp,
    /**
     * append vertical spacing as padding in the title not in the lazy column, cause [content] should not have content spaced by
     */
    title: (@Composable () -> Unit)? = null,
    /**
     * append vertical spacing as padding in the title not in the lazy column, cause [content] should not have content spaced by
     */
    subtitle: (@Composable () -> Unit)? = null,
    content: @Composable (i: Int) -> Unit,
) {

    val singleListShape = MDHorizontalCardGroupDefaults.shape
    val firstItemShape by remember(shape) {
        derivedStateOf {
            singleListShape.topOnly
        }
    }
    val lastItemShape by remember(shape) {
        derivedStateOf {
            singleListShape.bottomOnly
        }
    }
    val middleItemShape by remember {
        derivedStateOf {
            singleListShape.copy(CornerSize(0.dp))
        }
    }
    LazyColumn(
        modifier = modifier,
    ) {
        horizontalCardGroup(
            itemsCount = itemsCount,
            shape = shape,
            topOnlyShape = firstItemShape,
            bottomOnlyShape = lastItemShape,
            middleShape = middleItemShape,
            colors = colors,
            styles = styles,
            bottomHorizontalDividerThickness = bottomHorizontalDividerThickness,
            title = title,
            subtitle = subtitle,
            content = content
        )
    }
}