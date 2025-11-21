package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.SET_NULL
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagColor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagParent
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagPassColorToChildren
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagLabel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagTable

@Entity(
    tableName = dbTagTable,
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = [dbTagId],
            childColumns = [dbTagParent],
            onDelete = SET_NULL,
            onUpdate = SET_NULL,
        )
    ],
    indices = [
        Index(dbTagLabel, unique = true)
    ]
)
data class TagEntity(
    @ColumnInfo(dbTagId)
    @PrimaryKey(autoGenerate = true)
    val tagId: Long? = null,
    @ColumnInfo(dbTagLabel)
    val label: String,
    @ColumnInfo(dbTagParent)
    val parentId: Long? = null,
    @ColumnInfo(dbTagColor)
    val color: Int? = null,
    @ColumnInfo(dbTagPassColorToChildren)
    val passToChildren: Boolean = false,
)