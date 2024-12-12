package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.drawMeshBackgroundBehind

fun Modifier.drawChartBackground(
    outerMeshColor: Color,
    innerMeshColor: Color,
    rows: Int = 3,
    columns: Int = 3,
    innerMeshWidth: Dp = 1.dp,
    outerMeshWidth: Dp = 1.5.dp
) = aspectRatio(1f)
    .fillMaxWidth()
    .drawMeshBackgroundBehind(
        meshColor = innerMeshColor,
        columns = columns,
        rows = rows,
        meshWidth = innerMeshWidth
    )
    .border(
        width = outerMeshWidth,
        color = outerMeshColor
    )
