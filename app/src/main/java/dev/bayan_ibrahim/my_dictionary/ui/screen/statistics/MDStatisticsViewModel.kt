package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.instantIdentifier
import dev.bayan_ibrahim.my_dictionary.domain.model.date.startOf
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResultType
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
        viewModelScope.launch {
            _uiState.onExecute {
                _uiState.preferences = args.preferences
                _uiState.dateUnit = args.preferences.dateUnit

                val lineData = initPreferencesBarsData(args)
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

    private suspend fun MDStatisticsViewModel.initPreferencesBarsData(
        args: MDDestination.Statistics,
    ): Map<TrainWordResultType, Map<Int, Int>> = when (val pref = args.preferences) {
        is MDStatisticsViewPreferences.Date -> initDatePreferences(pref)
        is MDStatisticsViewPreferences.Train -> initTrainHistoryPreferences(pref)
        is MDStatisticsViewPreferences.Word -> initWordPreferences(pref)
        is MDStatisticsViewPreferences.Language -> initLanguagePreferences(pref)
        is MDStatisticsViewPreferences.Tag -> initTagPreferences(pref)
        is MDStatisticsViewPreferences.TypeTag -> initTypeTagPreferences(pref)
    }

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

    private suspend fun initDatePreferences(
        datePreferences: MDStatisticsViewPreferences.Date,
    ): MDStatisticsLineChartData {
        val now = Clock.System.now()
        val startTime = datePreferences.dateUnit.startOf(now)
        return repo.getTrainHistoryInDateRange(startTime, now).asLineChartData(datePreferences.dateUnit)
    }

    private suspend fun initTrainHistoryPreferences(
        trainHistoryPreferences: MDStatisticsViewPreferences.Train,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = repo.getMostRecentTrainHistory(
        trainHistoryPreferences.count.count
    ).asLineChartData(dateUnit)

    private suspend fun initWordPreferences(
        wordPreferences: MDStatisticsViewPreferences.Word,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = repo
        .getTrainHistoryOfWord(wordPreferences.wordId)
        .asLineChartData(dateUnit)

    private suspend fun initLanguagePreferences(
        languagePreferences: MDStatisticsViewPreferences.Language,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = repo
        .getTrainHistoryOfLanguage(languagePreferences.language)
        .asLineChartData(dateUnit)

    private suspend fun initTagPreferences(
        tagPreferences: MDStatisticsViewPreferences.Tag,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = repo
        .getTrainHistoryOfTag(tagPreferences.tag)
        .asLineChartData(dateUnit)

    private suspend fun initTypeTagPreferences(
        typeTagPreferences: MDStatisticsViewPreferences.TypeTag,
        dateUnit: MDDateUnit = uiState.dateUnit ?: MDStatisticsViewPreferences.DEFAULT_DATE_UNIT,
    ): MDStatisticsLineChartData = repo
        .getTrainHistoryOfTypeTag(typeTagPreferences.typeTagId)
        .asLineChartData(dateUnit)


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
            _uiState.dateUnit = unit
        }
    }
}
