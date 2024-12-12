package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Stable
fun Modifier.drawMeshBackgroundBehind(
    rows: Int = 20,
    columns: Int = 20,
    meshColor: Color,
    meshWidth: Dp = 2.dp,
) = drawBehind {
    drawMeshBackground(
        rows,
        columns,
        meshColor,
        meshWidth
    )
}

fun DrawScope.drawMeshBackground(
    rows: Int = 20,
    columns: Int = 20,
    meshColor: Color,
    meshWidth: Dp = 2.dp,
) {
    val rowsSpacing = size.height / rows
    val columnsSpacing = size.width / columns
    if (rows == columns) {
        (0..rows).forEach { row ->
            drawLineForRow(
                row = row,
                rowsSpacing = rowsSpacing,
                meshColor = meshColor,
                meshWidth = meshWidth
            )
            drawLineForColumn(
                column = row,
                columnsSpacing = columnsSpacing,
                meshColor = meshColor,
                meshWidth = meshWidth
            )
        }
    } else {
        (0..rows).forEach { row ->
            drawLineForRow(
                row = row,
                rowsSpacing = rowsSpacing,
                meshColor = meshColor,
                meshWidth = meshWidth
            )
        }
        (0..columns).forEach { column ->
            drawLineForColumn(
                column = column,
                columnsSpacing = columnsSpacing,
                meshColor = meshColor,
                meshWidth = meshWidth
            )
        }
    }
}

private fun DrawScope.drawLineForRow(
    row: Int,
    rowsSpacing: Float,
    meshColor: Color,
    meshWidth: Dp
) = drawLine(
    color = meshColor,
    start = Offset(0f, row * rowsSpacing),
    end = Offset(size.width, row * rowsSpacing),
    strokeWidth = meshWidth.toPx()
)

private fun DrawScope.drawLineForColumn(
    column: Int,
    columnsSpacing: Float,
    meshColor: Color,
    meshWidth: Dp
) = drawLine(
    color = meshColor,
    start = Offset(column * columnsSpacing, 0f),
    end = Offset(column * columnsSpacing, size.height),
    strokeWidth = meshWidth.toPx()
)

