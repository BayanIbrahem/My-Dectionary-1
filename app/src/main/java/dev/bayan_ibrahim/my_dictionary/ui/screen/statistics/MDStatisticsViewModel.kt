package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.instantIdentifier
import dev.bayan_ibrahim.my_dictionary.domain.model.date.startOf
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResultType
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDStatisticsRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class MDStatisticsViewModel @Inject constructor(
    private val repo: MDStatisticsRepo,
) : ViewModel() {
    private val _uiState: MDStatisticsMutableUiState = MDStatisticsMutableUiState()
    val uiState: MDStatisticsUiState = _uiState
    fun initWithNavArgs(args: MDDestination.Statistics) {
        _uiState.preferences = args.preferences
        _uiState.dateUnit = args.preferences.dateUnit
        onLoadPreferences()
    }

    private fun onLoadPreferences() {
        viewModelScope.launch {
            _uiState.onExecute {

                val trainHistoryRecords = getTrainHistoryRecord(uiState.preferences)
                _uiState.trainHistoryRecord.setAll(trainHistoryRecords)
                val lineData = getBarsData(uiState.preferences, trainHistoryRecords)
                val barData = lineData.toBarData()

                _uiState.lineChartData.clear()
                _uiState.barChartData.clear()

                _uiState.lineChartData.putAll(lineData)
                _uiState.barChartData.putAll(barData)

                val allTrainsCount = repo.getAllTrainsCount()
                _uiState.allTrainHistoryCount = allTrainsCount

                lineData.isNotEmpty()
            }
        }
    }

    private suspend fun getTrainHistoryRecord(preferences: MDStatisticsViewPreferences) = when (preferences) {
        is MDStatisticsViewPreferences.Date -> {
            val now = Clock.System.now()
            val startTime = preferences.dateUnit.startOf(now)
            repo.getTrainHistoryInDateRange(startTime, now)
        }

        is MDStatisticsViewPreferences.Language -> repo.getTrainHistoryOfLanguage(preferences.language)
        is MDStatisticsViewPreferences.Tag -> repo.getTrainHistoryOfTag(preferences.tagId)
        is MDStatisticsViewPreferences.Train -> repo.getMostRecentTrainHistory(preferences.count.count)
        is MDStatisticsViewPreferences.TypeTag -> repo.getTrainHistoryOfTypeTag(preferences.typeTagId)
        is MDStatisticsViewPreferences.Word -> repo.getTrainHistoryOfWord(preferences.wordId)
    }

    private suspend fun getBarsData(
        preferences: MDStatisticsViewPreferences,
        trainHistoryRecord: List<TrainHistory>,
    ): Map<MDTrainWordResultType, Map<Int, Int>> = when (preferences) {
        is MDStatisticsViewPreferences.Date -> initDatePreferences(trainHistoryRecord)
        is MDStatisticsViewPreferences.Train -> initTrainHistoryPreferences(trainHistoryRecord)
        is MDStatisticsViewPreferences.Word -> initWordPreferences(trainHistoryRecord)
        is MDStatisticsViewPreferences.Language -> initLanguagePreferences(trainHistoryRecord)
        is MDStatisticsViewPreferences.Tag -> initTagPreferences(trainHistoryRecord)
        is MDStatisticsViewPreferences.TypeTag -> initTypeTagPreferences(trainHistoryRecord)
    }


    private fun initDatePreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData {
        return trainHistoryRecord.asLineChartData(dateUnit)
    }

    private fun initTrainHistoryPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initWordPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initLanguagePreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initTagPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initTypeTagPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun Collection<TrainHistory>.asLineChartData(
        dateUnit: MDDateUnit,
    ) = this.map { train ->
        train.words.map { wordTrain ->
            train.time to wordTrain
        }
    }.flatten().groupBy { (time, train) ->
        train.trainResult.type
    }.mapValues { (result, words) ->
        words.groupBy { (time, word) ->
            dateUnit.instantIdentifier(time)
        }.mapValues { (identifier, words) ->
            words.count()
        }
    }

    private fun MDStatisticsLineChartData.toBarData(): MDStatisticsBarChartData = mapValues {
        it.value.values.sum()
    }

    fun getUiActions(
        navActions: MDStatisticsNavigationUiActions,
    ): MDStatisticsUiActions = MDStatisticsUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDStatisticsNavigationUiActions,
    ): MDStatisticsBusinessUiActions = object : MDStatisticsBusinessUiActions {
        override fun onSelectTrainHistoryCount(count: MDStatisticsMostResentHistoryCount) {
            _uiState.selectedTrainHistoryCount = count
        }

        override fun onSelectDateUnit(unit: MDDateUnit) {
            _uiState.preferences = _uiState.preferences.copyWith(unit)
            
            onLoadPreferences()
        }
    }
}
