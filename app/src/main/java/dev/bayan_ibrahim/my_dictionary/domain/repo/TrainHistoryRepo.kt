package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TrainHistoryRepo {
    /**
     * return train histories sorted desc by date
     */
    fun getTrainHistoryOf(
        startTime: Instant? = null,
        endTime: Instant? = null,
        wordsIds: Set<Long>? = null,
        limit: Int? = null,
    ): Flow<List<TrainHistory>>

    suspend fun getAllTrainsCount(): Int

    suspend fun submitTrainHistory(trainHistory: TrainHistory, wordsNewMemoryDecay: Map<Long, Float>)
}