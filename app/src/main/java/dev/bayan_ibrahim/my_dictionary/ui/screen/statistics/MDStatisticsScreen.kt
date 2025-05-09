package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.MDDateTimeFormat
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.format
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toDefaultLocalDateTime
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.asFormattedString
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.upperStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.card2Content
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.bar_chart.MDBarChart
import dev.bayan_ibrahim.my_dictionary.core.design_system.chart.line_chart.MDLineChart
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.format
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.labelOfIdentifier
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.WordTrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResultType
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.components.MDStatisticsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsChartTypeTab
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlin.math.roundToInt

@Composable
fun MDStatisticsScreen(
    uiState: MDStatisticsUiState,
    uiActions: MDStatisticsUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        invalidDataMessage = firstCapStringResource(R.string.no_history_hint),
        topBar = {
            MDStatisticsTopAppBar(
                preferences = uiState.preferences,
                availableTrainHistoryCount = uiState.allTrainHistoryCount,
                selectedTrainHistoryCount = uiState.selectedTrainHistoryCount,
                dateUnit = uiState.preferences.dateUnit,
                onSelectTrainHistoryCount = uiActions::onSelectTrainHistoryCount,
                onSelectDateUnit = uiActions::onSelectDateUnit,
                onNavigationIconClick = uiActions::onOpenNavDrawer,
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
        val dateUnit by remember(uiState.preferences) {
            derivedStateOf {
                uiState.preferences.dateUnit
            }
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                MDTabRow(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    tabs = MDStatisticsChartTypeTab.entries.map { it.tabData },
                    selectedTabIndex = selectedChart.ordinal,
                    onClickTab = { index, tab ->
                        selectedChart = tab ?: MDStatisticsChartTypeTab.entries[index]
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                HorizontalPager(
                    modifier = Modifier.sizeIn(maxWidth = 400.dp, maxHeight = 400.dp),// TODO, fix chart size using a better way
                    userScrollEnabled = false,
                    state = pagerState,
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
            if (uiState.trainHistoryRecord.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                uiState.trainHistoryRecord.forEach { trainHistory ->
                    if (trainHistory.words.isNotEmpty()) { // TODO, replace by filter from view model
                        card2Content(
                            contentCount = trainHistory.words.count(),
                            overline = {
                                val formattedTime by remember(
                                    trainHistory.time
                                ) {
                                    derivedStateOf {
                                        trainHistory.time.toDefaultLocalDateTime().format(MDDateTimeFormat.EuropeanDateTime)
                                    }
                                }
                                Text(
                                    text = formattedTime,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp)
                                )
                            },
                            header = {
                                val trainType by remember {
                                    derivedStateOf {
                                        trainHistory.words.first().trainType
                                    }
                                }
                                val resultsPercentages by remember(trainHistory.words) {
                                    derivedStateOf {
                                        trainHistory.words.groupBy {
                                            it.trainResult.type
                                        }.mapValues { (_, results) ->
                                            results.count().times(100f).div(trainHistory.words.count()).roundToInt().coerceIn(0, 100)
                                        }.entries.sortedBy { it.key.ordinal }
                                    }
                                }
                                MDCard2ListItem(
                                    modifier = Modifier,
                                    leadingIcon = { MDIcon(trainType.icon) },
                                    trailingIcon = {
                                        Text(
                                            text = "x${trainHistory.words.count().asFormattedString()}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    subtitle = resultsPercentages.map {
                                        "${it.key.label} %${it.value}"
                                    }.joinToString(" - "),
                                    title = trainType.label,
                                )
                            },
                        ) { i ->
                            val wordHistory by remember(i, trainHistory) {
                                derivedStateOf { trainHistory.words[i] }
                            }
                            MDCard2ListItem(
                                leading = {
                                    MDIcon(
                                        icon = wordHistory.trainResult.type.icon,
                                        tint = getTrainResultTypeColor(wordHistory.trainResult.type)
                                    )
                                },
                                subtitle = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(wordHistory.trainResult.submitOption.label)
                                        Spacer(modifier = Modifier.weight(1f))
                                        MDIcon(MDIconsSet.TrainTime, modifier = Modifier.size(16.dp))
                                        Text(wordHistory.trainResult.consumedDuration.format)
                                    }
                                }
                            ) {
                                MDWordHistoryContent(wordHistory)
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getTrainResultTypeColor(
    trainResultType: MDTrainWordResultType,
) = when (trainResultType) {
    MDTrainWordResultType.Pass -> MaterialTheme.colorScheme.outline
    MDTrainWordResultType.Right -> MaterialTheme.colorScheme.primary
    MDTrainWordResultType.Timeout -> MaterialTheme.colorScheme.outlineVariant
    MDTrainWordResultType.Wrong -> MaterialTheme.colorScheme.error
}

@Composable
private fun MDWordHistoryContent(
    wordHistory: WordTrainHistory,
    modifier: Modifier = Modifier,
) {
    val color = getTrainResultTypeColor(wordHistory.trainResult.type)
    when (wordHistory.trainResult) {
        is MDTrainWordResult.Pass -> {
            MDWordHistoryContent(
                question = wordHistory.questionWord,
                mainLabel = upperStringResource(R.string.pass),
                modifier = modifier,
                mainLabelColor = color,
                secondLabel = wordHistory.trainResult.correctAnswer,
                secondLabelColor = getTrainResultTypeColor(MDTrainWordResult.Right.type)
            )
        }

        is MDTrainWordResult.Timeout -> {
            MDWordHistoryContent(
                question = wordHistory.questionWord,
                modifier = modifier,
                mainLabel = upperStringResource(R.string.timeout),
                mainLabelColor = color,
                secondLabel = wordHistory.trainResult.correctAnswer,
                secondLabelColor = getTrainResultTypeColor(MDTrainWordResult.Right.type)
            )
        }

        is MDTrainWordResult.Right -> {
            MDWordHistoryContent(
                question = wordHistory.questionWord,
                mainLabel = wordHistory.trainResult.correctAnswer,
                modifier = modifier,
                mainLabelColor = color
            )
        }

        is MDTrainWordResult.Wrong -> {
            MDWordHistoryContent(
                question = wordHistory.questionWord,
                mainLabel = wordHistory.trainResult.selectedAnswer,
                modifier = modifier,
                mainLabelColor = color,
                mainLabelTextDecoration = TextDecoration.LineThrough,
                secondLabel = wordHistory.trainResult.correctAnswer,
                secondLabelColor = getTrainResultTypeColor(MDTrainWordResult.Right.type)
            )
        }
    }
}

@Composable
private fun MDWordHistoryContent(
    question: String,
    mainLabel: String,
    modifier: Modifier = Modifier,
    defaultStyle: TextStyle = LocalTextStyle.current,
    secondLabel: String? = null,
    mainLabelColor: Color? = null,
    mainLabelFontWeight: FontWeight? = FontWeight.Bold,
    mainLabelFontStyle: FontStyle? = FontStyle.Italic,
    mainLabelTextDecoration: TextDecoration = TextDecoration.None,
    secondLabelColor: Color? = null,
    secondLabelFontWeight: FontWeight? = FontWeight.Bold,
    secondLabelFontStyle: FontStyle? = FontStyle.Italic,
    secondLabelTextDecoration: TextDecoration = TextDecoration.None,
) {
    Text(
        text = buildAnnotatedString {
            append(question)
            append("  ")
            pushStyle(
                defaultStyle.copy(
                    fontWeight = mainLabelFontWeight,
                    fontStyle = mainLabelFontStyle,
                    textDecoration = mainLabelTextDecoration,
                    color = mainLabelColor ?: LocalTextStyle.current.color,
                ).toSpanStyle()
            )
            append(mainLabel)
            if (secondLabel != null) {
                pushStyle(defaultStyle.toSpanStyle())
                append("  ")
                pushStyle(
                    defaultStyle.copy(
                        fontWeight = secondLabelFontWeight,
                        fontStyle = secondLabelFontStyle,
                        textDecoration = secondLabelTextDecoration,
                        color = secondLabelColor ?: LocalTextStyle.current.color,
                    ).toSpanStyle()
                )
                append(secondLabel)
            }
        },
        modifier = modifier.basicMarquee(Int.MAX_VALUE),
    )
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
    val type = MDTrainWordResultType.entries[typeOrdinal]
    return when (type) {
        MDTrainWordResultType.Wrong -> error
        MDTrainWordResultType.Timeout -> onSurface
        MDTrainWordResultType.Pass -> primary
        MDTrainWordResultType.Right -> onSurface
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
                        object : MDStatisticsNavigationUiActions, MDAppNavigationUiActions {
                            override fun onOpenNavDrawer() {}
                            override fun onCloseNavDrawer() {}

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
