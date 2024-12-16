package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.line_chart

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.background.MDChartBackground
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.bar_chart.defaultColors
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.drawLines
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDLineChartContent(
    charts: List<List<Int>>,
    modifier: Modifier = Modifier,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    color: (index: Int) -> Color = { i ->
        defaultColors[i.mod(defaultColors.count())]
    },
    labelColor: Color = MaterialTheme.colorScheme.primary,
    min: Int = charts.minOf { it.min() },
    max: Int = charts.maxOf { it.max() },
    inputPointLabelVector: Offset = Offset(1f, 0f),
    pointSize: Dp = 4.dp,
    lineWidth: Dp = 1.dp,
    calculateHorizontalPadding: (width: Float) -> Pair<Float, Float> = { Pair(0f, 0f) },
    calculateVerticalPadding: (Float) -> Pair<Float, Float> = { Pair(0f, 0f) },
    pointsValuesHeight: Map<Int, Float> = emptyMap(),
) {
    var inputPoint: Offset? by remember {
        mutableStateOf(null)
    }
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(true) {
                this.awaitEachGesture {
                    val event = this.awaitPointerEvent()
                    inputPoint = event.changes.lastOrNull()?.position
                }
            }
            .drawLines(
                charts = charts,
                min = min,
                max = max,
                measurer = textMeasurer,
                color = color,
                labelColor = labelColor,
                inputPoint = inputPoint,
                inputPointLabelVector = inputPointLabelVector,
                pointSize = pointSize,
                lineWidth = lineWidth,
                calculateHorizontalPadding = calculateHorizontalPadding,
                calculateVerticalPadding = calculateVerticalPadding,
                pointsValuesHeight = pointsValuesHeight,
            )
    )
}

@Preview
@Composable
private fun MDSomeChartComponentPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val values = listOf(
                    listOf(1, 5, 2),
                    listOf(10, 4, 5),
                    listOf(1, 5, 2),
                )
                val yLabels = listOf(1, 2, 3)
                val xLabels = listOf(
                    "label 1",
                    "label 2",
                    "label 3",
                )
                MDChartBackground(
                    yLabels = yLabels,
                    xLabelRotationDegree = 60f,
                    xLabels = xLabels,
                    yLabelBlankEndPadding = 24.dp,
                ) {
                    MDLineChartContent(
                        charts = listOf(
                            listOf(1, 5, 2),
                            listOf(10, 4, 5),
                            listOf(1, 5, 2),
                        ),
                    )
                }
            }
        }
    }
}