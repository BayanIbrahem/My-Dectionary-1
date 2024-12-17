package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.bar_chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.background.MDChartBackground
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.drawBars
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlin.random.Random

val defaultColors = listOf(
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Yellow,
    Color.Cyan,
    Color.Magenta,
)

@Composable
fun MDBarChartContent(
    bars: List<Int>,
    modifier: Modifier = Modifier,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    color: (index: Int, value: Int) -> Color = { i, _ ->
        defaultColors[i.mod(defaultColors.count())]
    },
    labelColor: (index: Int, value: Int) -> Color = color,
    gapPercent: Float = 0.5f,
    valuePadding: Dp = 2.dp,
    radius: Dp = 4.dp,
    valueFormat: (Int) -> String = { it.asFormattedString() },
    pointsValuesHeight: Map<Int, Float> = emptyMap(),
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawBars(
            values = bars,
            textMeasurer = textMeasurer,
            color = color,
            labelColor = labelColor,
            gapPercent = gapPercent,
            valuePadding = valuePadding,
            radius = radius,
            valueFormat = valueFormat,
            pointsValuesHeight = pointsValuesHeight,
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
                MDChartBackground(
                    yLabels = listOf(0, 25, 50, 75, 100, 125),
                    xLabels = List(values.size) { i -> i.toString() }
                ) {
                    MDBarChartContent(bars = values)
                }
            }
        }
    }
}