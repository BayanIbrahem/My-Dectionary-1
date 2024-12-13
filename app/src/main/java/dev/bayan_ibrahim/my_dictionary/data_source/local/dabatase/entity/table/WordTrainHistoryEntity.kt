package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.TrainWordResultConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.TrainWordTypeConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryMeaningSnapshot
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTime
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTrainHistoryResult
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryTrainHistoryType
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTrainHistoryWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

@TypeConverters(
    TrainWordTypeConverter::class,
    TrainWordResultConverter::class,
)
@Entity(
    tableName = dbTrainHistoryTable,
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = [dbWordId],
            childColumns = [dbTrainHistoryWordId],
            onUpdate = ForeignKey.SET_NULL,
            onDelete = ForeignKey.SET_NULL,
        )
    ],
    // TODO, add train history to index
//    indices = [
//        Index(dbTrainHistoryWordId)
//    ]
)
data class TrainHistoryEntity(
    @ColumnInfo(dbTrainHistoryId)
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(dbTrainHistoryWordId)
    val wordId: Long,
    @ColumnInfo(dbTrainHistoryTime)
    val time: Long,
    @ColumnInfo(dbTrainHistoryMeaningSnapshot)
    val meaningSnapshot: String,
    @ColumnInfo(dbTrainHistoryTrainHistoryType)
    val trainType: TrainWordType,
    @ColumnInfo(dbTrainHistoryTrainHistoryResult)
    val trainResult: TrainWordResult,
)
