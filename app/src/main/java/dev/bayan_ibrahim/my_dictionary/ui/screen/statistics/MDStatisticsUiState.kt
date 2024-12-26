package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResultType
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences

typealias MDStatisticsBarChartData = Map<MDTrainWordResultType, Int>
typealias MDStatisticsLineChartData = Map<MDTrainWordResultType, Map<Int, Int>>

interface MDStatisticsUiState : MDUiState {
    val preferences: MDStatisticsViewPreferences
    val allTrainHistoryCount: Int
    val selectedTrainHistoryCount: MDStatisticsMostResentHistoryCount
    val dateUnit: MDDateUnit?

    val barChartData: MDStatisticsBarChartData
    val lineChartData: MDStatisticsLineChartData

    val trainHistoryRecord: List<TrainHistory>
}

class MDStatisticsMutableUiState : MDStatisticsUiState, MDMutableUiState() {
    override var preferences: MDStatisticsViewPreferences by mutableStateOf(MDStatisticsViewPreferences.Date())
    override var allTrainHistoryCount: Int by mutableIntStateOf(0)
    override var selectedTrainHistoryCount: MDStatisticsMostResentHistoryCount by mutableStateOf(MDStatisticsMostResentHistoryCount._1)
    override var dateUnit: MDDateUnit? by mutableStateOf(null)

    override val barChartData: SnapshotStateMap<MDTrainWordResultType, Int> = mutableStateMapOf()
    override val lineChartData: SnapshotStateMap<MDTrainWordResultType, Map<Int, Int>> = mutableStateMapOf()
    override val trainHistoryRecord: SnapshotStateList<TrainHistory> = mutableStateListOf()
}
