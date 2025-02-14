package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.instantIdentifier
import dev.bayan_ibrahim.my_dictionary.domain.model.date.startOf
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResultType
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainHistoryRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class MDStatisticsViewModel @Inject constructor(
    private val trainHistoryRepo: TrainHistoryRepo,
    private val wordRepo: WordRepo,
) : ViewModel() {
    private val _uiState: MDStatisticsMutableUiState = MDStatisticsMutableUiState()
    val uiState: MDStatisticsUiState = _uiState
    fun initWithNavArgs(args: MDDestination.Statistics) {
        _uiState.preferences = args.preferences
        onLoadPreferences()
    }

    private fun onLoadPreferences() {
        viewModelScope.launch {
            _uiState.onExecute {
                val endTime = Clock.System.now()
                val startTime = _uiState.preferences.dateUnit.startOf(endTime)

                val trainHistoryRecords = getTrainHistoryRecord(uiState.preferences, startTime, endTime)
                _uiState.trainHistoryRecord.setAll(trainHistoryRecords)
                val lineData = getBarsData(uiState.preferences, trainHistoryRecords)
                val barData = lineData.toBarData()

                _uiState.lineChartData.clear()
                _uiState.barChartData.clear()

                _uiState.lineChartData.putAll(lineData)
                _uiState.barChartData.putAll(barData)

                val allTrainsCount = trainHistoryRepo.getAllTrainsCount()
                _uiState.allTrainHistoryCount = allTrainsCount

                lineData.isNotEmpty()
            }
        }
    }

    private suspend fun getTrainHistoryRecord(
        preferences: MDStatisticsViewPreferences,
        startTime: Instant,
        endTime: Instant,
    ) = when (preferences) {
        is MDStatisticsViewPreferences.Date -> trainHistoryRepo.getTrainHistoryOf(startTime, endTime).first()
        is MDStatisticsViewPreferences.Language -> getTrainHistoryOfLanguage(preferences.language, startTime, endTime)
        is MDStatisticsViewPreferences.Tag -> getTrainHistoryOfTag(preferences.tag, startTime, endTime)
        is MDStatisticsViewPreferences.Train -> trainHistoryRepo.getTrainHistoryOf(limit = preferences.count.quantity).first()
        is MDStatisticsViewPreferences.WordClass -> getTrainHistoryOfWordClass(preferences.wordClassId, startTime, endTime)
        is MDStatisticsViewPreferences.Word -> getTrainHistoryOfWord(preferences.wordId, startTime, endTime)
    }

    private suspend fun getTrainHistoryOfWord(
        wordId: Long,
        startTime: Instant?,
        endTime: Instant?,
        limit: Int? = null,
    ): List<TrainHistory> = trainHistoryRepo.getTrainHistoryOf(
        startTime = startTime,
        endTime = endTime,
        wordsIds = setOf(wordId),
        limit = limit,
    ).first()

    private suspend fun getTrainHistoryOfLanguage(
        language: Language,
        startTime: Instant?,
        endTime: Instant?,
        limit: Int? = null,
    ): List<TrainHistory> {
        val wordsIds = wordRepo.getWordsIdsOf(languages = setOf(language)).first()
        return trainHistoryRepo.getTrainHistoryOf(
            startTime = startTime,
            endTime = endTime,
            wordsIds = wordsIds,
            limit = limit
        ).first()
    }

    private suspend fun getTrainHistoryOfTag(
        tag: Tag,
        startTime: Instant?,
        endTime: Instant?,
        limit: Int? = null,
    ): List<TrainHistory> {
        val wordsIds = wordRepo.getWordsIdsOf(tags = setOf(tag)).first()
        return trainHistoryRepo.getTrainHistoryOf(
            startTime = startTime,
            endTime = endTime,
            wordsIds = wordsIds,
            limit = limit
        ).first()
    }

    private suspend fun getTrainHistoryOfWordClass(
        wordClassId: Long,
        startTime: Instant?,
        endTime: Instant?,
        limit: Int? = null,
    ): List<TrainHistory> {
        val wordsIds = wordRepo.getWordsIdsOf(wordsClasses = setOf(wordClassId)).first()
        return trainHistoryRepo.getTrainHistoryOf(
            startTime = startTime,
            endTime = endTime,
            wordsIds = wordsIds,
            limit = limit
        ).first()
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
        is MDStatisticsViewPreferences.WordClass -> initWordClassPreferences(trainHistoryRecord)
    }


    private fun initDatePreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.preferences.dateUnit
    ): MDStatisticsLineChartData {
        return trainHistoryRecord.asLineChartData(dateUnit)
    }

    private fun initTrainHistoryPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.preferences.dateUnit,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initWordPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.preferences.dateUnit,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initLanguagePreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.preferences.dateUnit,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initTagPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.preferences.dateUnit,
    ): MDStatisticsLineChartData = trainHistoryRecord.asLineChartData(dateUnit)

    private fun initWordClassPreferences(
        trainHistoryRecord: List<TrainHistory>,
        dateUnit: MDDateUnit = uiState.preferences.dateUnit,
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
