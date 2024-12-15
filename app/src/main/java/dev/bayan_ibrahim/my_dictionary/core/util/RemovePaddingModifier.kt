package dev.bayan_ibrahim.my_dictionary.core.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun Modifier.removePadding(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
): Modifier = removePadding(
    paddingValues = PaddingValues(horizontal = horizontal, vertical = vertical),
    layoutDirection = LayoutDirection.Ltr,
)

fun Modifier.removePadding(
    padding: Dp,
    layoutDirection: LayoutDirection,
): Modifier = removePadding(
    paddingValues = PaddingValues(padding),
    layoutDirection = layoutDirection
)

/**
 * reduce width and height by vertical and horizontal padding,
 * place top left on (-startPadding, -topPadding) instead of (0, 0)
 */
fun Modifier.removePadding(
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection,
): Modifier {
    val topPadding = paddingValues.calculateTopPadding()
    val bottomPadding = paddingValues.calculateBottomPadding()
    val startPadding = paddingValues.calculateStartPadding(layoutDirection)
    val endPadding = paddingValues.calculateEndPadding(layoutDirection)
    return this.layout { measurer, constrains ->
        val placeable = measurer.measure(constrains)
        val topPaddingPx = topPadding.roundToPx()
        val bottomPaddingPx = bottomPadding.roundToPx()
        val startPaddingPx = startPadding.roundToPx()
        val endPaddingPx = endPadding.roundToPx()
        val width = placeable.width - startPaddingPx - endPaddingPx
        val height = placeable.height - topPaddingPx - bottomPaddingPx
        layout(width, height) {
            placeable.placeRelative(
                x = -startPaddingPx,
                y = -topPaddingPx
            )
        }
    }
}