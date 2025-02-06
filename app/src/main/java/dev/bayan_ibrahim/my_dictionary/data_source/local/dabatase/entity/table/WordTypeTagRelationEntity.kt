package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationLabel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTagId

@Entity(
    tableName = dbWordClassRelationTable,
    foreignKeys = [
        ForeignKey(
            entity = WordWordClassEntity::class,
            parentColumns = [dbWordClassId],
            childColumns = [dbWordClassRelationTagId],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(dbWordClassRelationTagId),
    ]
)
data class WordWordClassRelationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = dbWordClassRelationId)
    val id: Long? = null,
    @ColumnInfo(name = dbWordClassRelationLabel)
    val label: String,
    @ColumnInfo(name = dbWordClassRelationTagId)
    val tagId: Long,
)
