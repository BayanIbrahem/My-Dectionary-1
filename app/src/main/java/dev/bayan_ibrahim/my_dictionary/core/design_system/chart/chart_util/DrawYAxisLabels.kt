package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput

fun Modifier.drawYLabels(
    range: IntRange,
    labelsCount: Int,
    textMeasurer: TextMeasurer,
    color: Color,
    textStyle: TextStyle,
    bottomPadding: Dp = 15.dp,
): Modifier {
    val labels = calculateYLabels(range, labelsCount)
    return drawYLabels(
        values = labels,
        textMeasurer = textMeasurer,
        color = color,
        textStyle = textStyle,
        bottomPadding = bottomPadding,
    )
}

fun calculateYLabels(
    range: IntRange,
    labelsCount: Int,
): List<Int> {
    val rangeLength = range.let { it.last - it.first }.inc()
    if (labelsCount > rangeLength)
        return calculateYLabels(range = range, labelsCount = rangeLength)
    val (min, max, step) = calculateYAxisStepsFields(range, labelsCount)
    val first = range.first - step
    return List(labelsCount + 2) { i ->
        (first + (i * step)).coerceIn(min, max)
    }.reversed().distinct()
}

fun calculateYAxisStepsFields(
    valueRange: IntRange,
    labelsCount: Int,
): Triple<Int?, Int, Int> {
    val step = (valueRange.last - valueRange.first) / (labelsCount - 1)
    val min = calculateMinStep(step, valueRange.first).takeIf { it < valueRange.first }
    val max = calculateMaxStep(step, valueRange.last)
    return Triple(min, max, step)
}

private fun calculateMinStep(
    step: Int,
    actualMin: Int,
    coercedAtLeastTo: Int = 0,
): Int = (actualMin - step).coerceAtLeast(coercedAtLeastTo)

private fun calculateMaxStep(
    step: Int,
    actualMax: Int,
    coercedAtMostTo: Int = Int.MAX_VALUE,
): Int = (actualMax + step).coerceAtMost(coercedAtMostTo)

fun Modifier.drawYLabels(
    values: List<Int>,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    color: Color = textStyle.color,
    labelFormat: (Int) -> String = {
        it.asFormattedString()
    },
    bottomPadding: Dp = 15.dp,
) = drawWithCache {
    val result = values.map {
        textMeasurer.measure(text = labelFormat(it), style = textStyle.copy(color = color))
    }
    drawYLabels(yLabels = result, bottomPadding.toPx())
}

fun Modifier.drawYLabels(
    values: List<TextLayoutResult>,
    bottomPadding: Dp = 15.dp,
) = drawWithCache {
    drawYLabels(yLabels = values, bottomPadding.toPx())
}

private fun CacheDrawScope.drawYLabels(
    yLabels: List<TextLayoutResult>,
    bottomPadding: Float = 15.dp.toPx(),
) = onDrawBehind {
    drawLabelsForIntRangeOnXAxis(
        values = yLabels,
        bottomPadding = bottomPadding
    )
}

fun DrawScope.drawLabelsForIntRangeOnXAxis(
    values: List<TextLayoutResult>,
    bottomPadding: Float = 15.dp.toPx(),
) {
    val top = 0f
    val bottom = size.height - bottomPadding
    val count = values.count()
    // 0..5 ->
    // 0 - 1 - 3 - 5 - 7 - 8
    // (i-1)*2.coerceIn(0, 2i-2))
    values.forEachIndexed { index, value ->
        drawText(
            textLayoutResult = value,
            topLeft = Offset(
                x = 0f,
                y = calculateYOutput(index, count, top, bottom)
            ),
        )
    }
}

/**
 * calculate position of this label according to [top], [bottom] taking into account that the first
 * or last step would be half steps
 * @see [mapYLabelIndex]
 */
fun calculateYOutput(
    index: Int,
    count: Int,
    top: Float = 0f,
    bottom: Float,
): Float = calculateOutput(
    input = mapYLabelIndex(index, count).toFloat(),
    inputRangeStart = 0f,
    inputRangeEnd = mapYLabelIndex(count.dec(), count).toFloat(),
    outputRangeStart = top,
    outputRangeEnd = bottom
)

/**
 * for 5 labels (with extra start and end labels) this would map range from 0..4 to 0..6
 * where each non edge step would be +2 so values would be mapped from
 * [0, 1, 2, 3, 4] -> [0, 1, 3, 5, 6]
 */
fun mapYLabelIndex(
    i: Int,
    count: Int,
): Int = (2 * i - 1).coerceIn(0, 2 * count.dec() - 2)