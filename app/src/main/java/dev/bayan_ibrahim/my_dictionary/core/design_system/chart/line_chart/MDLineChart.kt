package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.line_chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.background.MDChartBackground
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.bar_chart.defaultColors
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.calculateYLabels
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.calculateYOutput
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.calculateYPositionsForValues
import kotlin.math.abs

@Composable
fun MDLineChart(
    charts: List<List<Int>>,
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
    yLabelColor: Color = MaterialTheme.colorScheme.onBackground,
    yLabelBlankEndPadding: Dp = 1.dp,
    // mesh
    outerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    innerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    rows: Int = 3,
    columns: Int = 3,
    innerMeshWidth: Dp = 1.dp,
    outerMeshWidth: Dp = 1.5.dp,
    // chart
    chartColor: (index: Int) -> Color = { i ->
        defaultColors[i.mod(defaultColors.count())]
    },
    chartsMinValue: Int = charts.minOf { it.min() },
    chartsMaxValue: Int = charts.maxOf { it.max() },
    pointSize: Dp = 4.dp,
    chartLineWidth: Dp = 1.dp,
    inputPointLabelVector: Offset = Offset(1f, 0f),
    chartLabelColor: Color = MaterialTheme.colorScheme.primary,
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
//    val calculateVerticalPadding: (height: Float) -> Pair<Float, Float> by remember(yLabelsValues, chartsMinValue, chartsMaxValue) {
//        derivedStateOf {
//            with(density) {
//                { height ->
//                    val yLabelHeight = yLabelTextStyle.lineHeight.toPx()
//
//                    calculateChartContentVerticalPadding(
//                        yLabelHeight = yLabelHeight,
//                        yLabels = yLabelsValues,
//                        min = chartsMinValue,
//                        max = chartsMaxValue,
//                        height = height
//                    )
//                }
//            }
//        }
//    }
    val edgedXLabels by remember(xLabelsText) {
        derivedStateOf {
            xLabelsText.takeIf {
                it.isNotEmpty()
            }?.let {
                it.first() to it.last()
            }
        }
    }
    val horizontalPadding: (width: Float) -> Pair<Float, Float> by remember(edgedXLabels) {
        derivedStateOf {
            {
                edgedXLabels?.let { (first, second) ->
                    Pair(
                        first.size.width / 2f,
                        second.size.width / 2f,
                    )
                } ?: Pair(0f, 0f)
            }
        }
    }
    val pointsValuesHeight by remember(charts, yLabelsValues) {
        derivedStateOf {
            calculateYPositionsForValues(
                values = charts.flatten(),
                yLabelsValues = yLabelsValues
            )
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
    ) {
        MDLineChartContent(
            charts = charts,
            modifier = modifier,
            textMeasurer = textMeasurer,
            color = chartColor,
            labelColor = chartLabelColor,
            min = chartsMinValue,
            max = chartsMaxValue,
            inputPointLabelVector = inputPointLabelVector,
            pointSize = pointSize,
            lineWidth = chartLineWidth,
            calculateHorizontalPadding = horizontalPadding,
//            calculateVerticalPadding = calculateVerticalPadding,
            pointsValuesHeight = pointsValuesHeight,
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
