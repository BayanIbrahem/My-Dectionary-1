package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationLabel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId

@Entity(
    tableName = dbTypeTagRelatedWordTable,
    foreignKeys = [
        ForeignKey(
            entity = WordTypeTagRelationEntity::class,
            parentColumns = [dbTypeTagRelationLabel],
            childColumns = [dbTypeTagRelatedWordRelationId],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = [dbWordId],
            childColumns = [dbTypeTagRelatedWordBaseWordId],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(dbTypeTagRelatedWordRelationId),
        Index(dbTypeTagRelatedWordBaseWordId),
    ]
)
data class WordTypeTagRelatedWordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = dbTypeTagRelatedWordId)
    val id: Long? = null,
    @ColumnInfo(name = dbTypeTagRelatedWordRelationId)
    val relationLabel: String,
    @ColumnInfo(name = dbTypeTagRelatedWordBaseWordId)
    val baseWordId: Long,
    @ColumnInfo(name = dbTypeTagRelatedWordName)
    val word: String,
)