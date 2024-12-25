package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagLanguage
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode

@Entity(
    tableName = dbTypeTagTable,
    foreignKeys = [
        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = [dbLanguageCode],
            childColumns = [dbTypeTagLanguage],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(dbTypeTagLanguage)
    ]
)
data class WordTypeTagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbTypeTagId)
    val id: Long? = null,
    @ColumnInfo(dbTypeTagName)
    val name: String,
    @ColumnInfo(dbTypeTagLanguage)
    val language: String,
)