package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme
import kotlin.math.max
import kotlin.math.min

/**
 * @param state lazy list state of the scrollable item
 * @param crossDirectionBarValue width of the bar for vertical scrolling, or height of the bar for horizontal scrolling,
 * orientation is taken from `state.layoutInfo.orientation`
 * @param minMainBarDimensionValue the min height (for vertical scrolling) or width (for horizontal scrolling) of the bar,
 * originally, if there are too much items the bar main axis will be so small, so this value is used to make give it min size
 * @param activeScrollingAlpha alpha while scroll is in progress `state.isScrollInProgress`
 * @param inactiveScrollingAlpha alpha while scroll is not in progress `state.isScrollInProgress`
 * @param activeScrollingDuration duration of animation while scroll is in progress `state.isScrollInProgress`
 * @param inactiveScrollingDuration duration of animation while scroll is not in progress `state.isScrollInProgress`
 * @param defaultColor this param will be ignored if [drawScrollbar] is overwritten
 * @param stickHeadersContentType pass the content type for the sticky headers to be filtered from the list, using sticky headers without passing the
 * correct value to this param makes the scrollbar at the first item position (which is the last sticky header)
 * @param drawScrollbar this is the lambda to draw the bar, if it is overridden  the [defaultColor] value will be ignored,
 * the bar will be clipped according to the `topLeft` and `barSize`, and `alpha` is the animated value of visibility
 */
@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    crossDirectionBarValue: Dp = 8.dp,
    minMainBarDimensionValue: Dp = crossDirectionBarValue * 1.6f,
    activeScrollingAlpha: Float = 1f,
    inactiveScrollingAlpha: Float = 0f,
    activeScrollingDuration: Int = 150,
    inactiveScrollingDuration: Int = 500,
    defaultColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    stickHeadersContentType: Any? = null,
    drawScrollbar: DrawScope.(topLeft: Offset, barSize: Size, alpha: Float) -> Unit = { topLeft, barSize, alpha ->
        drawRect(
            color = defaultColor,
            topLeft = topLeft,
            size = barSize,
            alpha = alpha
        )
    },
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) activeScrollingAlpha else inactiveScrollingAlpha
    val duration = if (state.isScrollInProgress) activeScrollingDuration else inactiveScrollingDuration

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "alpha"
    )

    return drawWithContent {
        drawContent()

        onDrawScrollbar(
            state = state,
            alpha = alpha,
            crossDirectionValue = crossDirectionBarValue,
            minMainBarDimensionValue = minMainBarDimensionValue,
            drawScrollbar = drawScrollbar,
            stickHeadersContentType = stickHeadersContentType,
            orientation = state.layoutInfo.orientation,
        )
    }
}

private fun ContentDrawScope.onDrawScrollbar(
    state: LazyListState,
    alpha: Float,
    crossDirectionValue: Dp,
    minMainBarDimensionValue: Dp,
    orientation: Orientation,
    stickHeadersContentType: Any?,
    drawScrollbar: DrawScope.(topLeft: Offset, size: Size, alpha: Float) -> Unit,
) {
    val (sizeMainDimension, sizeCrossDimension) = when (orientation) {
        Orientation.Vertical -> size.height to size.width
        Orientation.Horizontal -> size.width to size.height
    }
    val nonStickyHeadersVisibleItems = state.layoutInfo.visibleItemsInfo.let {
        if (it.size < 2) {
            return@let it
        } else {
            val isFirstItemIsAStickyHeader = it.first().contentType == stickHeadersContentType
            val firstItemInItsNormalPosition = it.first().index.inc() == it[1].index
            if (isFirstItemIsAStickyHeader && !firstItemInItsNormalPosition) {
                // the for example if the visible sticky header index is 0 and the first visible item is 2 then we must filter the sticky header
                return@let it.subList(1, it.size)
            } else {
                return@let it
            }
        }
    }
    val firstVisibleElementIndex = nonStickyHeadersVisibleItems.firstOrNull()?.index
    val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

    // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
    if (needDrawScrollbar && firstVisibleElementIndex != null) {
        val elementMainDimensionValue = sizeMainDimension / state.layoutInfo.totalItemsCount
        val originalScrollbarMainDimensionValue = nonStickyHeadersVisibleItems.size * elementMainDimensionValue
        val minScrollbarMainDimensionValue = minMainBarDimensionValue.toPx()

        val originalScrollbarOffsetMainDimension = firstVisibleElementIndex * elementMainDimensionValue

        val minScrollbarMainDimensionMaxAvailableSpace = sizeMainDimension - minScrollbarMainDimensionValue
        val originalMaxAvailableSpace = sizeMainDimension - originalScrollbarMainDimensionValue
        val minScrollbarOffsetMainDimension =
            originalScrollbarOffsetMainDimension * (minScrollbarMainDimensionMaxAvailableSpace / originalMaxAvailableSpace)

        val scrollbarOffsetMainDimension = min(minScrollbarOffsetMainDimension, originalScrollbarOffsetMainDimension)
        val scrollbarMainDimensionValue = max(minScrollbarMainDimensionValue, originalScrollbarMainDimensionValue)
        val (topLeft, size) = when (orientation) {
            Orientation.Vertical -> Offset(
                x = sizeCrossDimension - crossDirectionValue.toPx(),
                y = scrollbarOffsetMainDimension
            ) to Size(crossDirectionValue.toPx(), scrollbarMainDimensionValue)

            Orientation.Horizontal -> Offset(
                x = scrollbarOffsetMainDimension,
                y = sizeCrossDimension - crossDirectionValue.toPx()
            ) to Size(scrollbarMainDimensionValue, crossDirectionValue.toPx())
        }
        clipRect(
            left = topLeft.x, top = topLeft.y, bottom = topLeft.y + size.height
        ) {
            drawScrollbar(topLeft, size, alpha)
        }
    }
}

@Preview
@Composable
private fun VerticalScrollbarPreview() {
    MyDictionaryTheme() {
        val lazyListState = rememberLazyListState()
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollbar(lazyListState, minMainBarDimensionValue = 50.dp),
                state = lazyListState,
            ) {
                items(1000) { i ->
                    Text("Item $i", modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun HorizontalScrollbarPreview() {
    MyDictionaryTheme() {
        val lazyListState = rememberLazyListState()
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollbar(lazyListState),
                state = lazyListState,
            ) {
                items(100) { i ->
                    Text("Item $i", modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
