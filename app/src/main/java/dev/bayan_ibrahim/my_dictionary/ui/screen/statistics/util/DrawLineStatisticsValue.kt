package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import kotlin.math.roundToInt

/**
 * @param charts [chart1, chart2, ...], chart_i = [point1, point2, ...]
 * @param min, like [max], default value can be used, but it is recommended to pass a lifecycle cached
 * value of them to avoid recalculating them
 * @param max see [min]
 */
fun Modifier.drawLines(
    charts: List<List<Int>>,
    min: Int = charts.minOf { it.min() },
    max: Int = charts.maxOf { it.max() },
    measurer: TextMeasurer,
    color: (index: Int) -> Color,
    labelColor: Color,
    inputPoint: Offset? = null,
    /**
     * where will point label will appear according to the chart point
     */
    inputPointLabelVector: Offset = Offset(1f, 0f),
    pointSize: Dp = 4.dp,
    lineWidth: Dp = 1.dp,
    calculateHorizontalPadding: (width: Float) -> Pair<Float, Float> = { Pair(0f, 0f) },
    calculateVerticalPadding: (height: Float) -> Pair<Float, Float> = { Pair(0f, 0f) },
) = drawWithCache {
    // specs
    val count = charts.requireMatrix() ?: return@drawWithCache onDrawBehind {} // empty list of charts
    val fcount = count.toFloat()
    val chartsCount = charts.count()
    val fmin = min.toFloat()
    val fmax = max.toFloat()
    val hPaddingPx = calculateHorizontalPadding(size.width)
    val vPaddingPx = calculateVerticalPadding(size.height)
    val pointSizePx = pointSize.toPx()
    val lineWidthPx = lineWidth.toPx()
    val chartsPoints = generateValuesPoints(charts, fcount, hPaddingPx, fmin, fmax, vPaddingPx)

    val inputIndex = inputPoint?.let { input ->
        calculateOutput(
            input = input.x,
            inputRangeStart = hPaddingPx.first,
            inputRangeEnd = size.width - hPaddingPx.second,
            outputRangeStart = 0f,
            outputRangeEnd = fcount - 1,
        ).roundToInt().coerceIn(0, count - 1)
    }
    val inputLabels = generateInputPoints(
        chartsValues = charts,
        chartsPoints = chartsPoints,
        measurer = measurer,
        pointIndex = inputIndex,
        inputPointLabelVector = inputPointLabelVector,
        chartsCount = chartsCount
    )
    onDrawBehind {
        chartsPoints.forEachIndexed { i, chart ->
            drawLine(
                points = chart,
                color = color(i),
                pointSize = pointSizePx,
                lineWidth = lineWidthPx,
                inputIndex = inputIndex,
            )
        }
        drawLabels(
            labels = inputLabels,
            color = labelColor,
        )
    }
}

private fun generateInputPoints(
    chartsValues: List<List<Int>>,
    chartsPoints: List<List<Offset>>,
    measurer: TextMeasurer,
    pointIndex: Int?,
    inputPointLabelVector: Offset,
    chartsCount: Int,
): List<Pair<Offset, TextLayoutResult>>? = pointIndex?.let { i ->
    val inputVectorUnit = inputPointLabelVector.asVectorUnit()
    List(chartsCount) { ch ->
        val value = chartsValues[ch][i]
        val point = chartsPoints[ch][i]
        val textLayoutResult = measurer.measure(value.asFormattedString())
        val (w, h) = textLayoutResult.size
        val labelTopLeft = Offset(
            x = point.x + (inputVectorUnit.x * w) - w / 2,
            y = point.y + (inputVectorUnit.y * h) - h / 2,
        )
        labelTopLeft to textLayoutResult
    }
}


fun List<List<Int>>.requireMatrix(): Int? {
    var c: Int? = null
    require(
        all {
            if (c == null) {
                c = it.count()
            }
            it.count() == c
        }
    ) {
        "invalid matrix, not all inner lists are equal size"
    }
    return c
}

private fun CacheDrawScope.generateValuesPoints(
    values: List<List<Int>>,
    count: Float,
    hPaddingPx: Pair<Float, Float>,
    min: Float,
    max: Float,
    vPaddingPx: Pair<Float, Float>,
): List<List<Offset>> {
    val xOutputEnd = size.width - hPaddingPx.second
    val yOutputStart = size.height - vPaddingPx.second
    return values.mapIndexed { _, chart ->
        chart.mapIndexed { i, value ->
            Offset(
                x = calculateOutput(
                    input = i.toFloat(),
                    inputRangeStart = 0f,
                    inputRangeEnd = count - 1,
                    outputRangeStart = hPaddingPx.first,
                    outputRangeEnd = xOutputEnd
                ),
                y = calculateOutput(
                    input = value.toFloat(),
                    inputRangeStart = min,
                    inputRangeEnd = max,
                    outputRangeStart = yOutputStart,
                    outputRangeEnd = vPaddingPx.second,
                )
            )
        }
    }
}

fun DrawScope.drawLine(
    values: List<Int>,
    color: Color,
    pointSize: Dp = 4.dp,
    lineWidth: Dp = 1.dp,
    minValue: Int = values.min(),
    maxValue: Int = values.max(),
    hPadding: Pair<Dp, Dp> = Pair(12.dp, 12.dp),
    vPadding: Pair<Dp, Dp> = Pair(6.dp, 6.dp),
) {
    val min = minValue.toFloat()
    val max = maxValue.toFloat()
    val count = values.count().toFloat()
    val hPaddingPx = Pair(hPadding.first.toPx(), hPadding.second.toPx())
    val vPaddingPx = Pair(vPadding.first.toPx(), vPadding.second.toPx())
    val points = values.mapIndexed { i, value ->
        val x = calculateOutput(
            input = i.toFloat(),
            inputRangeStart = 0f,
            inputRangeEnd = count - 1,
            outputRangeStart = hPaddingPx.first,
            outputRangeEnd = size.width - (hPaddingPx.first + hPaddingPx.second)
        )
        val y = calculateOutput(
            input = value.toFloat(),
            inputRangeStart = min,
            inputRangeEnd = max,
            outputRangeEnd = vPaddingPx.second,
            outputRangeStart = size.height - vPaddingPx.second
        )
        Offset(x, y)
    }
    drawLine(
        points = points,
        color = color,
        pointSize = pointSize.toPx(),
        lineWidth = lineWidth.toPx(),
        inputIndex = null
    )
}

private fun DrawScope.drawLine(
    points: List<Offset>,
    color: Color,
    pointSize: Float = 4.dp.toPx(),
    lineWidth: Float = 1.dp.toPx(),
    inputIndex: Int? = null,
) {
    val path = Path()
    var lastPoint: Offset = Offset.Zero
    val bezier = CubicBezier.easyInOut()
    points.forEachIndexed { i, point ->
        if (i == 0) {
            path.moveTo(point)
            lastPoint = point
        } else {
            path.cubicToBy(lastPoint, point, bezier)
            lastPoint = point
        }
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(lineWidth)
    )
    val first = points.firstOrNull()
    val last = points.lastOrNull()
    first?.let { f ->
        last?.let { l ->
            path.lineTo(l.x, size.height)
            path.lineTo(f.x, size.height)
            path.close()
        }
    }
    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(color, Color.Transparent)
        ),
        style = Fill
    )
    drawPoints(
        points = points,
        pointMode = PointMode.Points,
        color = color,
        strokeWidth = pointSize,
        cap = StrokeCap.Round,
    )
    inputIndex?.let { i ->
        drawPoints(
            points = listOf(points[i]),
            pointMode = PointMode.Points,
            color = color,
            strokeWidth = pointSize * 2,
            cap = StrokeCap.Round,
        )
    }
}


fun Path.moveTo(offset: Offset) = moveTo(offset.x, offset.y)
fun Path.lineTo(offset: Offset) = lineTo(offset.x, offset.y)

fun Path.cubicToBy(from: Offset, to: Offset, bezier: CubicBezier) {
    val c1 = bezier.controlPoint1Of(from, to)
    val c2 = bezier.controlPoint2Of(from, to)
    cubicTo(
        x1 = c1.x, y1 = c1.y,
        x2 = c2.x, y2 = c2.y,
        x3 = to.x, y3 = to.y,
    )
}

fun DrawScope.drawLabels(
    labels: List<Pair<Offset, TextLayoutResult>>?,
    color: Color,
) {
    labels?.forEach { (topLeft, label) ->
        drawText(textLayoutResult = label, color = color, topLeft = topLeft)
    }
}

data class CubicBezier(
    val controlPoint1: Offset,
    val controlPoint2: Offset,
) {
    fun controlPoint1Of(from: Offset, to: Offset): Offset = weightedAvg(from, to, controlPoint1)
    fun controlPoint2Of(from: Offset, to: Offset): Offset = weightedAvg(from, to, controlPoint2)
    private fun weightedAvg(
        p1: Offset,
        p2: Offset,
        weight: Offset,
    ): Offset {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return Offset(
            x = dx * weight.x + p1.x,
            y = dy * weight.y + p1.y,
        )
    }

    companion object Companion {
        fun easyInOut() = CubicBezier(
            controlPoint1 = Offset(.42f, 0f),
            controlPoint2 = Offset(.58f, 1f)
        )
    }
}
