package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput

/**
 * @param values the values list of bars
 * @param color the color of each bar, index, index in sorted order (descending), and values are passed
 * @param yLabelsCount count of y labels
 * @param gapPercent the gap percent from the bar width, for example if the value is 0.5f then the
 * gap between any two bars is half of the bar width, this will ensure to fit the bars whatever
 * ths available width is
 * @param valuePadding vertical padding between the value label and the bar
 * @param radius corner radius of the bar
 * @param translate x-axis and y-axis translation of the whole bars
 */
fun Modifier.drawBars(
    values: List<Int>,
    textMeasurer: TextMeasurer,
    color: (index: Int, value: Int) -> Color,
    labelColor: (index: Int, value: Int) -> Color,
    gapPercent: Float = 0.5f,
    valuePadding: Dp = 2.dp,
    radius: Dp = 4.dp,
    valueFormat: (Int) -> String = { it.asFormattedString() },
) = drawBehind {
    drawBars(
        values = values,
        textMeasurer = textMeasurer,
        valueFormat = valueFormat,
        color = color,
        labelColor = labelColor,
        gapPercent = gapPercent,
        valuePadding = valuePadding,
        radius = radius,
    )
}

fun DrawScope.drawBars(
    values: List<Int>,
    textMeasurer: TextMeasurer,
    color: (index: Int, value: Int) -> Color,
    labelColor: (index: Int, value: Int) -> Color,
    gapPercent: Float = 0.5f,
    valuePadding: Dp = 2.dp,
    radius: Dp = 4.dp,
    valueFormat: (Int) -> String = { it.asFormattedString() },
    pointsValuesHeight: Map<Int, Float> = emptyMap(),
) {
    drawBars(
        values = values,
        measureValue = { value ->
            textMeasurer.measure(valueFormat(value))
        },
        color = color,
        labelColor = labelColor,
        gapPercent = gapPercent,
        valuePadding = valuePadding.toPx(),
        cornerRadius = CornerRadius(radius.toPx()),
        pointsValuesHeight = pointsValuesHeight,
    )
}

/**
 * return half bar width using [calculateBarWidth]
 */
fun calculateBarChartXLabelHorizontalPaddingPx(
    totalWidth: Float,
    barsCount: Int,
    gapPercent: Float,
): Float {
    val barWidth = calculateBarWidth(totalWidth = totalWidth, count = barsCount, gapPercent = gapPercent)
    return barWidth / 2
}

private fun DrawScope.drawBars(
    values: List<Int>,
    measureValue: (Int) -> TextLayoutResult,
    color: (index: Int, value: Int) -> Color,
    labelColor: (index: Int, value: Int) -> Color,
    gapPercent: Float = 0.5f,
    valuePadding: Float = 2.dp.toPx(),
    cornerRadius: CornerRadius = CornerRadius(4.dp.toPx()),

    pointsValuesHeight: Map<Int, Float> = emptyMap(),
) {
    val count = values.count()
    if (count == 0) return
    val barWidth = calculateBarWidth(size.width, values.count(), gapPercent)
//    val (min, max, _) = calculateYAxisStepsFields(
//        valueRange = values.min()..values.max(),
//        labelsCount = yLabelsCount
//    )
    values.mapIndexed { i, value ->
        Bar(
            length = calculateOutput(
                input = pointsValuesHeight[value] ?: 0f,
                inputRangeStart = 0f,
                inputRangeEnd = 1f,
                outputRangeEnd = size.height,
                outputRangeStart = 0f,
            ),
            barColor = color(i, value),
            horizontalCenter = calculateBarHorizontalCenter(i, count, size.width, barWidth).takeUnless { it.isNaN() } ?: size.center.x,
            value = measureValue(value),
            labelColor = labelColor(i, value),
        )
    }.forEachIndexed { i, bar ->
        drawBar(
            bar = bar,
            width = barWidth,
            padding = valuePadding,
            cornerRadius = cornerRadius,
        )
    }
}

fun calculateBarWidth(
    totalWidth: Float,
    count: Int,
    gapPercent: Float,
): Float {
    if (count == 1) {
        // b + 2g= w
        // g = p * b
        // b + 2pb = w
        // b(1 +2p) = w
        // b = w/(1+2p)
        return totalWidth.div((1 + 2 * gapPercent))
    }
    return totalWidth / (count + gapPercent * count.dec())
}

fun calculateBarHorizontalCenter(
    index: Int,
    count: Int,
    width: Float,
    barWidth: Float,
): Float = (barWidth / 2) + index * ((width - barWidth) / count.dec())


/**
 * @param bar bar info (which is different from bar to bar
 * @param width bar width
 * @param labelColor label color
 * @param padding padding between label and bar top
 * @param cornerRadius radius of bar corner
 * @param translate x axis and y axis translation
 */
private fun DrawScope.drawBar(
    bar: Bar,
    width: Float,
    padding: Float = 2.dp.toPx(),
    cornerRadius: CornerRadius = CornerRadius(4.dp.toPx()),
) {
    val rectTopLeft = barTopLeft(size.height, width, bar.length, bar.horizontalCenter)
    val rectBottomEnd = barBottomEnd(size.height, width, bar.horizontalCenter)
    val size = rectSize(rectTopLeft, rectBottomEnd)
    val valueTopLeft = bar.value.valueTopLeft(
        rectTop = rectTopLeft.y, rectBottomCenter = bar.horizontalCenter, padding = padding
    )
    drawText(
        textLayoutResult = bar.value,
        color = bar.labelColor,
        topLeft = valueTopLeft,
    )
    drawRoundRect(
        brush = bar.barColor.barBrush(
            topLeft = rectTopLeft,
            bottomEnd = rectBottomEnd,
        ),
        topLeft = rectTopLeft,
        cornerRadius = cornerRadius,
        size = size,
    )
}

private fun barTopLeft(
    totalHeight: Float,
    width: Float,
    length: Float,
    horizontalCenter: Float,
): Offset = Offset(
    x = horizontalCenter - (width / 2), y = totalHeight - length
)

private fun barBottomEnd(
    totalHeight: Float,
    width: Float,
    horizontalCenter: Float,
): Offset = Offset(
    x = horizontalCenter + (width / 2), y = totalHeight
)

private fun rectSize(
    topLeft: Offset,
    bottomEnd: Offset,
): Size = Size(
    width = bottomEnd.x - topLeft.x, height = bottomEnd.y - topLeft.y
)

private fun TextLayoutResult.valueTopLeft(
    rectTop: Float,
    rectBottomCenter: Float,
    padding: Float,
): Offset = Offset(
    x = rectBottomCenter - size.width / 2,
    y = rectTop - padding - size.height,
)

private fun Color.barBrush(
    topLeft: Offset,
    bottomEnd: Offset,
) = Brush.linearGradient(
    colors = listOf(
        this,
        this.copy(0.5f),
    ),
//    start = topLeft,
//    end = bottomEnd,
)


private data class Bar(
    val length: Float,
    val horizontalCenter: Float,
    val value: TextLayoutResult,
    val barColor: Color,
    val labelColor: Color,
)