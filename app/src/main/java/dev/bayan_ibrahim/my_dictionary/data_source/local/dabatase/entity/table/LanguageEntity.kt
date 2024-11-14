package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageTable

@Entity(
    tableName = dbLanguageTable
)
data class LanguageEntity(
    @PrimaryKey
    @ColumnInfo(dbLanguageCode)
    val code: String,
)