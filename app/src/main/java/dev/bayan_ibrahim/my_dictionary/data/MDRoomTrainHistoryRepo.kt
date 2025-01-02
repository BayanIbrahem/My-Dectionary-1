package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryEntities
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryModels
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainHistoryRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class MDRoomTrainHistoryRepo(
    private val db: MDDataBase,
) : TrainHistoryRepo {
    private val trainHistoryDao: TrainHistoryDao = db.getWordTrainDao()
    private val wordDao: WordDao = db.getWordDao()
    override fun getTrainHistoryOf(
        startTime: Instant?,
        endTime: Instant?,
        wordsIds: Set<Long>?,
        limit: Int?,
    ): Flow<List<TrainHistory>> = trainHistoryDao.getTrainHistoryOf(
        includeTime = startTime != null || endTime != null,
        startTime = startTime?.toEpochMilliseconds() ?: 0L,
        endTime = endTime?.toEpochMilliseconds() ?: Long.MAX_VALUE,
        includeWordsIds = !wordsIds.isNullOrEmpty(),
        wordsIds = wordsIds ?: emptySet(),
    ).map { entities ->
        val models = entities.toTrainHistoryModels()
        if (limit != null && limit < models.count()) {
            models.subList(0, limit)
        } else {
            models
        }
    }

    override suspend fun getAllTrainsCount(): Int {
        return trainHistoryDao.getTotalTrainHistoryCount()
    }
    override suspend fun submitTrainHistory(
        trainHistory: TrainHistory,
        wordsNewMemoryDecay: Map<Long, Float>,
    ) {
        val entities = trainHistory.toTrainHistoryEntities()
        db.withTransaction {
            trainHistoryDao.insertTrainHistories(entities)
            val newLastTrainTime = trainHistory.time.toEpochMilliseconds()
            wordsNewMemoryDecay.forEach { (id, decay) ->
                wordDao.updateLastTrainHistoryAndMemoryDecay(id, newLastTrainTime, decay)
            }
        }
    }
}