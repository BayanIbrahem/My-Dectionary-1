package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset

const val DefaultRotationDegree = 60f
fun Modifier.drawXLabels(
    labels: List<String>,
    textMeasurer: TextMeasurer,
    style: TextStyle,
    color: Color = style.color,
    horizontalPadding: (
        width: Float,
        firstLabelWidth: Int,
        lastLabelWidth: Int,
    ) -> Pair<Float, Float> = { t, f, l -> Pair(0f, 0f) },
    rotationDegree: Float = DefaultRotationDegree,
) = drawXLabels(
    labels = labels.map { textMeasurer.measure(it, style.copy(color)) },
    rotationDegree = rotationDegree,
    horizontalPadding = horizontalPadding
)

fun Modifier.drawXLabels(
    labels: List<TextLayoutResult>,
    rotationDegree: Float = DefaultRotationDegree,
    horizontalPadding: (
        width: Float,
        firstLabelWidth: Int,
        lastLabelWidth: Int,
    ) -> Pair<Float, Float> = { t, f, l -> Pair(0f, 0f) },
): Modifier = drawBehind {
    drawXLabels(
        labels = labels,
        rotationDegree = rotationDegree,
        horizontalPadding = horizontalPadding(size.width, labels.first().size.width, labels.last().size.width)
    )

}

private fun DrawScope.drawXLabels(
    labels: List<TextLayoutResult>,
    rotationDegree: Float = DefaultRotationDegree,
    horizontalPadding: Pair<Float, Float> = Pair(0f, 0f),
) {
    val count = labels.count()
    val (startPadding, endPadding) = horizontalPadding
    val horizontalEmptySpace = size.width - labels.sumOf { it.size.width }
    val spacedBy = (horizontalEmptySpace - startPadding - endPadding) / count.dec()
    var prevEnd: Float = startPadding - spacedBy // we subtract spacedBy because it would be added for first dev.bayan_ibrahim.my_dictionary.core.design_system.group.item
    labels.forEachIndexed { i, label ->
        val halfWidth = label.size.width / 2
        val center = prevEnd + spacedBy + halfWidth
        prevEnd = center + halfWidth
        drawXLabel(
            label = label,
            center = center,
            rotationDegree = rotationDegree
        )
    }
}

fun DrawScope.drawXLabel(
    label: TextLayoutResult,
    center: Float,
    rotationDegree: Float = DefaultRotationDegree,
) {
    val centerOffset = label.calculateCenter(center, size.height)
    translate(
        left = centerOffset.x - label.size.width / 2,
        top = centerOffset.y + label.size.height
    ) {
        rotate(rotationDegree, label.size.center.toOffset()) {
            drawText(textLayoutResult = label)
        }
    }
}

private fun TextLayoutResult.calculateCenter(center: Float, height: Float): Offset = Offset(
    x = center,
    y = height - size.height / 2,
)
