package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagColor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagPassColorToChildren
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagPath
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagTable

@Entity(
    tableName = dbContextTagTable,
    indices = [
        Index(dbContextTagPath, unique = true)
    ]
)
data class ContextTagEntity(
    @ColumnInfo(dbContextTagId)
    @PrimaryKey(autoGenerate = true)
    val tagId: Long? = null,
    @ColumnInfo(dbContextTagPath)
    val path: String,
    @ColumnInfo(dbContextTagColor)
    val color: Int? = null,
    @ColumnInfo(dbContextTagPassColorToChildren)
    val passToChildren: Boolean = false,
)