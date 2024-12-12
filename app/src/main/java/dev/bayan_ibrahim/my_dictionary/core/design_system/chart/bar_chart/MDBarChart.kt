package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.bar_chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.background.MDChartBackground
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.calculateYLabels
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.calculateYOutput
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.calculateBarChartXLabelHorizontalPaddingPx
import kotlin.math.abs

@Composable
fun MDBarChart(
    bars: List<Int>,
    yRange: IntRange,
    yLabelsCount: Int,
    xLabels: List<String>,
    modifier: Modifier = Modifier,
    density: Density = LocalDensity.current,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    // x
    xLabelRotationDegree: Float = 0f,
    xLabelTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    xLabelColor: Color = MaterialTheme.colorScheme.onBackground,
    // y
    valueFormat: (Int) -> String = { it.asFormattedString() },
    yLabelTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    yLabelBlankEndPadding: Dp = 1.dp,
    yLabelColor: Color = MaterialTheme.colorScheme.onBackground,
    // mesh
    outerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    innerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    rows: Int = 3,
    columns: Int = 3,
    innerMeshWidth: Dp = 1.dp,
    outerMeshWidth: Dp = 1.5.dp,
    // chart
    barColor: (index: Int, value: Int) -> Color = { i, _ ->
        defaultColors[i.mod(defaultColors.count())]
    },
    barLabelColor: (index: Int, value: Int) -> Color = barColor,
    /**
     * percent between bar width and gap between two bars, if the values is 0.5  then the gap
     * would be half width of the bar
     */
    barGapPercent: Float = 0.5f,
    /**
     * padding between top of the bar and its value label at top
     */
    barValuePadding: Dp = 2.dp,
    barCornerRadius: Dp = 4.dp,
) {
    val yLabelsValues by remember(yRange) {
        derivedStateOf {
            calculateYLabels(yRange, yLabelsCount)
        }
    }
    val yLabelsText by remember(yLabelsValues) {
        derivedStateOf {
            yLabelsValues.map {
                textMeasurer.measure(valueFormat(it), style = yLabelTextStyle.copy(color = yLabelColor))
            }
        }
    }
    val xLabelsText by remember(xLabels) {
        derivedStateOf {
            xLabels.map {
                textMeasurer.measure(it, style = xLabelTextStyle.copy(color = xLabelColor))
            }
        }
    }

    MDChartBackground(
        yLabels = yLabelsText,
        xLabels = xLabelsText,
        modifier = modifier,
        density = density,
        yLabelBlankEndPadding = yLabelBlankEndPadding,
        outerMeshColor = outerMeshColor,
        innerMeshColor = innerMeshColor,
        rows = rows,
        columns = columns,
        innerMeshWidth = innerMeshWidth,
        outerMeshWidth = outerMeshWidth,
        xLabelRotationDegree = xLabelRotationDegree,
//        xLabelHorizontalPadding = { width, firstLabelWidth, lastLabelWidth ->
//            val padding = calculateBarChartXLabelHorizontalPaddingPx(
//                totalWidth = width,
//                barsCount = bars.count(),
//                gapPercent = barGapPercent
//            )
//            val fPadding = padding - firstLabelWidth.div(2)
//            val lPadding = padding - lastLabelWidth.div(2)
//            fPadding.coerceAtLeast(0f) to lPadding.coerceAtLeast(0f)
//        }
    ) {
        MDBarChartContent(
            bars = bars,
            yLabelsCount = yLabelsCount,
            textMeasurer = textMeasurer,
            color = barColor,
            labelColor = barLabelColor,
            gapPercent = barGapPercent,
            valuePadding = barValuePadding,
            radius = barCornerRadius,
            valueFormat = valueFormat
        )
    }
}

private fun calculateChartContentVerticalPadding(
    yLabelHeight: Float,
    yLabels: List<Int>,
    min: Int,
    max: Int,
    height: Float,
): Pair<Float, Float> {
    val labelPadding = yLabelHeight / 2

    val closestYLabelToMin = yLabels.minBy { abs(it - min) }
    val closestYLabelToMax = yLabels.minBy { abs(it - max) }
    val yLabelIndexOfMin = yLabels.indexOf(closestYLabelToMin)
    val yLabelIndexOfMax = yLabels.indexOf(closestYLabelToMax)

    val topPadding = calculateYOutput(
        index = yLabelIndexOfMax,
        count = 3,
        bottom = height
    ) + labelPadding

    val bottomPadding = calculateYOutput(
        index = yLabelIndexOfMin,
        count = 3,
        bottom = height
    ) - labelPadding

    return Pair(topPadding, bottomPadding)
}
