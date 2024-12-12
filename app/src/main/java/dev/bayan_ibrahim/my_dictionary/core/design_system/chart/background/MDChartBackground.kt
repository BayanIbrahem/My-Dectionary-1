package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.DefaultRotationDegree
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.drawChartBackground
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.drawXLabels
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util.drawYLabels
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.drawBars
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlin.random.Random

@Composable
fun MDChartBackground(
    yLabels: List<Int>,
    xLabels: List<String>,
    modifier: Modifier = Modifier,
    xLabelTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    yLabelTextStyle: TextStyle = MaterialTheme.typography.labelSmall,

    xLabelColor: Color = xLabelTextStyle.color,
    yLabelColor: Color = yLabelTextStyle.color,

    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    xLabelRotationDegree: Float = DefaultRotationDegree,
    density: Density = LocalDensity.current,
    /**
     * blank space width between mesh and the longest y label text
     */
    yLabelBlankEndPadding: Dp = 1.dp,

    // mesh
    outerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    innerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    rows: Int = 3,
    columns: Int = 3,
    innerMeshWidth: Dp = 1.dp,
    outerMeshWidth: Dp = 1.5.dp,

    /**
     * calculate horizontal padding according to available width
     * this padding would be added to the start or end of the label so it is necessary to take label width into account
     */
    xLabelHorizontalPadding: (
        width: Float,
        firstLabelWidth: Int,
        lastLabelWidth: Int,
    ) -> Pair<Float, Float> = { t, f, l -> Pair(f / 2f, l / 2f) },
    content: @Composable BoxScope.() -> Unit,
) {
    MDChartBackground(
        yLabels = yLabels.map {
            textMeasurer.measure(text = it.asFormattedString(), style = yLabelTextStyle.copy(color = yLabelColor))
        },
        xLabels = xLabels.map {
            textMeasurer.measure(text = it, style = xLabelTextStyle.copy(color = xLabelColor))
        },
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
        xLabelHorizontalPadding = xLabelHorizontalPadding,
        content = content,
    )
}

@Composable
fun MDChartBackground(
    yLabels: List<TextLayoutResult>,
    xLabels: List<TextLayoutResult>,
    modifier: Modifier = Modifier,
    density: Density = LocalDensity.current,
    /**
     * blank space width between mesh and the longest y label text
     */
    yLabelBlankEndPadding: Dp = 1.dp,

    // mesh
    outerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    innerMeshColor: Color = MaterialTheme.colorScheme.onBackground,
    rows: Int = 3,
    columns: Int = 3,
    innerMeshWidth: Dp = 1.dp,
    outerMeshWidth: Dp = 1.5.dp,

    xLabelRotationDegree: Float = DefaultRotationDegree,
    /**
     * calculate horizontal padding according to available width
     * this padding would be added to the start or end of the label so it is necessary to take label width into account
     */
    xLabelHorizontalPadding: (
        width: Float,
        firstLabelWidth: Int,
        lastLabelWidth: Int,
    ) -> Pair<Float, Float> = { t, f, l -> Pair(f / 2f, l / 2f) },
    content: @Composable BoxScope.() -> Unit,
) {
    val yLabelPadding by remember(yLabels) {
        derivedStateOf {
            yLabels.maxOf { it.size.width }.div(density.density).dp + yLabelBlankEndPadding
        }
    }
    Box(
        modifier = modifier
            .drawYLabels(
                values = yLabels,
                bottomPadding = 0.dp // TODO, calculate correct value
            )
            .padding(start = yLabelPadding)
            .drawXLabels(
                labels = xLabels,
                horizontalPadding = xLabelHorizontalPadding,
                rotationDegree = xLabelRotationDegree,
            )
    ) {
        Box(
            modifier = Modifier
                .drawChartBackground(
                    outerMeshColor = outerMeshColor,
                    innerMeshColor = innerMeshColor,
                    rows = rows,
                    columns = columns,
                    innerMeshWidth = innerMeshWidth,
                    outerMeshWidth = outerMeshWidth
                ),
            content = content
        )
    }
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
                val values = List(5) {
                    Random.nextInt(0, 100)
                }
                val yRange = values.min()..values.max()
                val yLabelsCount = minOf(yRange.last - yRange.first + 1, 5)
                MDChartBackground(
                    yLabels = listOf(0, 1, 2, 3, 4),
                    xLabels = List(5) {
                        "l ${it.inc()}"
                    },
                ) {
                    val textMeasurer = rememberTextMeasurer()
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawBars(
                            values = values,
                            textMeasurer = textMeasurer,
                            color = { index, value ->
                                Color.Red
                            },
                            yLabelsCount = yLabelsCount,
                            labelColor = { _, _ ->
                                Color.DarkGray
                            }
                        )
                    }
                }
            }
        }
    }
}