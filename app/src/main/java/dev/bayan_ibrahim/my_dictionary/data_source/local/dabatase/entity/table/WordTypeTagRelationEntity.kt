package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationLabel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationTagId

@Entity(
    tableName = dbTypeTagRelationTable,
    foreignKeys = [
        ForeignKey(
            entity = WordTypeTagEntity::class,
            parentColumns = [dbTypeTagId],
            childColumns = [dbTypeTagRelationTagId],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(dbTypeTagRelationTagId)
    ]
)
data class WordTypeTagRelationEntity(
    @PrimaryKey()
    @ColumnInfo(name = dbTypeTagRelationLabel)
    val label: String,
    @ColumnInfo(name = dbTypeTagRelationTagId)
    val tagId: Long,
)
