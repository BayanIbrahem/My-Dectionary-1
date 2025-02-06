package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagColor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagPassColorToChildren
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagPath
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagTable

@Entity(
    tableName = dbTagTable,
    indices = [
        Index(dbTagPath, unique = true)
    ]
)
data class TagEntity(
    @ColumnInfo(dbTagId)
    @PrimaryKey(autoGenerate = true)
    val tagId: Long? = null,
    @ColumnInfo(dbTagPath)
    val path: String,
    @ColumnInfo(dbTagColor)
    val color: Int? = null,
    @ColumnInfo(dbTagPassColorToChildren)
    val passToChildren: Boolean = false,
)