package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsWordId

@Entity(
    tableName = dbWordsCrossTagsTable,
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            childColumns = [dbWordsCrossTagsWordId],
            parentColumns = [dbWordId],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),

        ForeignKey(
            entity = TagEntity::class,
            childColumns = [dbWordsCrossTagsTagId],
            parentColumns = [dbTagId],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(dbWordsCrossTagsWordId),
        Index(dbWordsCrossTagsTagId),
    ]
)
data class WordCrossTagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbWordsCrossTagsId)
    val id: Long? = null,
    @ColumnInfo(dbWordsCrossTagsWordId)
    val wordId: Long,
    @ColumnInfo(dbWordsCrossTagsTagId)
    val tagId: Long,
)
