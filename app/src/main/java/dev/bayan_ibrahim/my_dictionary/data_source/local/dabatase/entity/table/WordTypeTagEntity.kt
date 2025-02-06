package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassLanguage
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode

@Entity(
    tableName = dbWordClassTable,
    foreignKeys = [
        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = [dbLanguageCode],
            childColumns = [dbWordClassLanguage],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(dbWordClassLanguage)
    ]
)
data class WordWordClassEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbWordClassId)
    val id: Long? = null,
    @ColumnInfo(dbWordClassName)
    val name: String,
    @ColumnInfo(dbWordClassLanguage)
    val language: String,
)