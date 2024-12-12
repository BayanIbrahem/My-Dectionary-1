package com.qubit_team.apps.focusflow.core.ui.common.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @param position the position according to the point
 * - 0  1  2
 * - 3  p  4
 * - 5  6  7
 */
fun Modifier.drawPoint(
    point: Offset?,
    value: Int?,
    measurer: TextMeasurer,
    color: Color,
    padding: Dp = 0.dp,
    position: Int = 3,
) = drawBehind {
    point?.let {
        value?.let {
            drawPoint(
                point = point,
                label = measurer.measure(value.toString()),
                color = color,
                padding = padding.toPx(),
                position = position % 8,
            )
        }
    }
}

private fun DrawScope.drawPoint(
    point: Offset,
    label: TextLayoutResult,
    color: Color,
    padding: Float,
    position: Int,
) {
    drawText(label, color, label.calculateTopLeft(point, padding, position))
}


private fun TextLayoutResult.calculateTopLeft(
    point: Offset,
    padding: Float,
    position: Int,
): Offset {
    val dx: Float
    val dy: Float
    when (position) {
        0 -> {
            dx = -size.width / 2 - padding
            dy = -size.height / 2 - padding
        }

        1 -> {
            dx = 0f
            dy = -size.height / 2 - padding
        }

        2 -> {
            dx = +size.width / 2 + padding
            dy = -size.height / 2 - padding
        }

        3 -> {
            dx = -size.width / 2 - padding
            dy = 0f
        }

        4 -> {
            dx = +size.width / 2 + padding
            dy = 0f
        }

        5 -> {
            dx = -size.width / 2 - padding
            dy = +size.height / 2 + padding
        }

        6 -> {
            dx = 0f
            dy = +size.height / 2 + padding
        }

        else -> {
            dx = +size.width / 2 + padding
            dy = +size.height / 2 + padding
        }
    }
    return Offset(
        x = point.x - size.width / 2 + dx,
        y = point.y - size.height / 2 + dy,
    )
}