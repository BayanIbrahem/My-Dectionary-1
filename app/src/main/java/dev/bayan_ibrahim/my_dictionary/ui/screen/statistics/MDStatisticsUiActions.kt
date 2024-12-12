package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit

interface MDStatisticsBusinessUiActions {
    fun onSelectTrainHistoryCount(count: MDStatisticsMostResentHistoryCount)
    fun onSelectDateUnit(unit: MDDateUnit)
}

interface MDStatisticsNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDStatisticsUiActions(
    navigationActions: MDStatisticsNavigationUiActions,
    businessActions: MDStatisticsBusinessUiActions,
) : MDStatisticsBusinessUiActions by businessActions, MDStatisticsNavigationUiActions by navigationActions