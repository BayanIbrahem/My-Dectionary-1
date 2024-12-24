package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsWordId

@Entity(
    tableName = dbWordsCrossContextTagsTable,
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            childColumns = [dbWordsCrossContextTagsWordId],
            parentColumns = [dbWordId],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),

        ForeignKey(
            entity = ContextTagEntity::class,
            childColumns = [dbWordsCrossContextTagsTagId],
            parentColumns = [dbContextTagId],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(dbWordsCrossContextTagsWordId),
        Index(dbWordsCrossContextTagsTagId),
    ]
)
data class WordCrossContextTagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbWordsCrossContextTagsId)
    val id: Long? = null,
    @ColumnInfo(dbWordsCrossContextTagsWordId)
    val wordId: Long,
    @ColumnInfo(dbWordsCrossContextTagsTagId)
    val tagId: Long,
)
