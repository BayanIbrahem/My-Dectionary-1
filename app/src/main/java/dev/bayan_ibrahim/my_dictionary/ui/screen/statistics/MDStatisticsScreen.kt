package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.bar_chart.MDBarChart
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.line_chart.MDLineChart
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.labelOfIdentifier
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResultType
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.components.MDStatisticsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsChartTypeTab
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDStatisticsScreen(
    uiState: MDStatisticsUiState,
    uiActions: MDStatisticsUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        invalidDataMessage = "No History available, you should train first",
        topBar = {
            MDStatisticsTopAppBar(
                preferences = uiState.preferences,
                availableTrainHistoryCount = uiState.allTrainHistoryCount,
                selectedTrainHistoryCount = uiState.selectedTrainHistoryCount,
                dateUnit = uiState.dateUnit,
                onSelectTrainHistoryCount = uiActions::onSelectTrainHistoryCount,
                onSelectDateUnit = uiActions::onSelectDateUnit,
            )
        },
    ) {
        var selectedChart by remember {
            mutableStateOf(MDStatisticsChartTypeTab.BAR)
        }
        val pagerState = rememberPagerState {
            MDStatisticsChartTypeTab.entries.count()
        }
        LaunchedEffect(selectedChart) {
            pagerState.animateScrollToPage(selectedChart.ordinal)
        }
        val dateUnit by remember(uiState.dateUnit) {
            derivedStateOf {
                uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT
            }
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MDTabRow(
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                tabs = MDStatisticsChartTypeTab.entries.map { it.tabData },
                selectedTabIndex = selectedChart.ordinal,
                onClickTab = { index, tab ->
                    selectedChart = tab ?: MDStatisticsChartTypeTab.entries[index]
                }
            )

            HorizontalPager(
                modifier = Modifier,
                state = pagerState
            ) {
                when (selectedChart) {
                    MDStatisticsChartTypeTab.LINE -> LineChartData(
                        chartData = uiState.lineChartData,
                        dateUnit = dateUnit
                    )

                    MDStatisticsChartTypeTab.BAR -> BarChartData(uiState.barChartData)
                }
            }
        }
    }
}

@Composable
private fun LineChartData(
    chartData: MDStatisticsLineChartData,
    dateUnit: MDDateUnit,
    modifier: Modifier = Modifier,
) {
    val yRange by remember(chartData) {
        derivedStateOf {
            chartData.values.let { lines ->
                val min = lines.minOf { line -> line.values.min() }
                val max = lines.maxOf { line -> line.values.max() }
                min..max
            }
        }
    }
    val xLabelsIdentifiers by remember(chartData) {
        derivedStateOf {
            chartData.values
                .map(Map<Int, Int>::keys)
                .flatten()
                .distinct()
                .sorted().let {
                    if (it.count() == 1) {
                        it + it // if it is a single value then we draw a line between two points that have same y
                    } else {
                        it
                    }
                }
        }
    }
    val charts by remember(chartData) {
        derivedStateOf {
            val xLabelsCount = xLabelsIdentifiers.count()
            chartData.entries.sortedBy {
                it.key
            }.map { (_, chart) ->
                List(xLabelsCount) { i ->
                    val identifier = xLabelsIdentifiers[i]
                    chart[identifier] ?: 0 // if the line doesn't have the data for this identifier then it would be get 0
                }
            }
        }
    }

    val xLabels = xLabelsIdentifiers.map {
        dateUnit.labelOfIdentifier(it)
    }

    val error = MaterialTheme.colorScheme.error
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary

    MDLineChart(
        charts = charts,
        yRange = yRange,
        yLabelsCount = 3,
        xLabels = xLabels,
        modifier = modifier,
        chartColor = {
            getColorOfType(
                typeOrdinal = it,
                error = error,
                onSurface = onSurface,
                primary = primary
            )
        }
    )
}

private fun getColorOfType(
    typeOrdinal: Int,
    error: Color,
    onSurface: Color,
    primary: Color,
): Color {
    val type = TrainWordResultType.entries[typeOrdinal]
    return when (type) {
        TrainWordResultType.Fail -> error
        TrainWordResultType.Timeout -> onSurface
        TrainWordResultType.Pass -> primary
    }
}

@Composable
private fun BarChartData(
    chartData: MDStatisticsBarChartData,
    modifier: Modifier = Modifier,
) {
    val yRange by remember(chartData) {
        derivedStateOf {
            chartData.values.let { lines ->
                lines.min()..lines.max()
            }
        }
    }
    val xLabelsIdentifiers by remember(chartData) {
        derivedStateOf {
            chartData.keys.sorted()
        }
    }
    val xLabels = xLabelsIdentifiers.map {
        it.label
    }

    val bars by remember(chartData) {
        derivedStateOf {
            chartData.entries.sortedBy {
                it.key
            }.map {
                it.value
            }
        }
    }

    val error = MaterialTheme.colorScheme.error
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary

    MDBarChart(
        bars = bars,
        yRange = yRange,
        yLabelsCount = 3,
        xLabels = xLabels,
        modifier = modifier,
        barColor = { i, _ ->
            getColorOfType(i, error, onSurface, primary)
        }
    )
}

@Preview
@Composable
private fun MDStatisticsScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDStatisticsScreen(
                    uiState = MDStatisticsMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = MDStatisticsUiActions(
                        object : MDStatisticsNavigationUiActions {
                        },
                        object : MDStatisticsBusinessUiActions {
                            override fun onSelectTrainHistoryCount(count: MDStatisticsMostResentHistoryCount) {}
                            override fun onSelectDateUnit(unit: MDDateUnit) {}
                        },
                    )
                )
            }
        }
    }
}
