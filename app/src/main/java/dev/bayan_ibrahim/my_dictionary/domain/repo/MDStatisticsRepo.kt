package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import kotlinx.datetime.Instant

interface MDStatisticsRepo {
    suspend fun getTrainHistoryInDateRange(
        start: Instant,
        end: Instant,
    ): List<TrainHistory>

    suspend fun getMostRecentTrainHistory(count: Int): List<TrainHistory>

    suspend fun getTrainHistoryOfWord(wordId: Long): List<TrainHistory>
    suspend fun getTrainHistoryOfLanguage(language: Language): List<TrainHistory>
    suspend fun getTrainHistoryOfTag(tag: Long): List<TrainHistory>
    suspend fun getTrainHistoryOfTypeTag(typeTagId: Long): List<TrainHistory>

    suspend fun getAllTrainsCount(): Int
}