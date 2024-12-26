package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.TrainWordTypeConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TrainHistoryEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTime
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTrainHistoryType
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryWordId
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(
    TrainWordTypeConverter::class
)
interface TrainHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrainHistories(entities: List<TrainHistoryEntity>)

    @Query(
        """DELETE FROM $dbTrainHistoryTable"""
    )
    fun deleteAllTrainHistory()

    @Query(
        """
            DELETE FROM $dbTrainHistoryTable WHERE $dbTrainHistoryTime BETWEEN :startTime AND :endTime
        """
    )
    fun deleteTrainHistoryInTimeRange(startTime: Long, endTime: Long)

    @Query(
        """
            DELETE FROM $dbTrainHistoryTable WHERE $dbTrainHistoryTrainHistoryType IN (:types)
        """
    )
    fun deleteTrainHistoryOfTypes(types: List<TrainWordType>)

    @Query(
        """
            SELECT * FROM $dbTrainHistoryTable
            WHERE (
                NOT :includeTime OR $dbTrainHistoryTime BETWEEN :startTime AND :endTime
            ) AND (
                NOT :includeTrainType OR $dbTrainHistoryTrainHistoryType IN (:trainTypes)
            ) AND (
                NOT :includeWordsIds OR $dbTrainHistoryWordId IN (:wordsIds)
            ) AND (
                NOT :includeExcludedWordsIds OR $dbTrainHistoryWordId NOT IN (:excludedWordsIds)
            )
            ORDER BY $dbTrainHistoryTime DESC
        """
    )
    fun getTrainHistoryOf(
        includeTime: Boolean = false,
        startTime: Long = 0,
        endTime: Long = Long.MAX_VALUE,
        includeTrainType: Boolean = false,
        trainTypes: Set<TrainWordType> = emptySet(),
        includeWordsIds: Boolean = false,
        wordsIds: Set<Long> = emptySet(),
        includeExcludedWordsIds: Boolean = false,
        excludedWordsIds: Set<Long> = emptySet(),
    ): Flow<List<TrainHistoryEntity>>

    @Query(
        """
        SELECT COUNT(DISTINCT $dbTrainHistoryTime / :timeGroupByFactor) FROM $dbTrainHistoryTable
    """
    )
    suspend fun getTotalTrainHistoryCount(
        timeGroupByFactor: Long = 60_000,
    ): Int
}