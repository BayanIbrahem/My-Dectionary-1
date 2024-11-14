package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordAdditionalTranslations
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordExamples
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLearningProgress
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTags
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranscription
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTypeTag

@Entity(
    tableName = dbWordTable,
    foreignKeys = [
        ForeignKey(
            entity = WordTypeTagEntity::class,
            parentColumns = [dbTypeTagId],
            childColumns = [dbWordTypeTag],
            onUpdate = ForeignKey.SET_NULL,
            onDelete = ForeignKey.SET_NULL,
        ),

        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = [dbLanguageCode],
            childColumns = [dbWordLanguageCode],
            onUpdate = ForeignKey.RESTRICT,
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(dbWordTypeTag),
        Index(dbWordLanguageCode),
    ]
)
@TypeConverters(StringListConverter::class)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbWordId)
    val id: Long? = null,
    @ColumnInfo(dbWordMeaning)
    val meaning: String,
    @ColumnInfo(dbWordTranslation)
    val translation: String,
    @ColumnInfo(dbWordLanguageCode)
    val languageCode: String,
    @ColumnInfo(dbWordAdditionalTranslations)
    val additionalTranslations: List<String> = emptyList(),
    @ColumnInfo(dbWordTags)
    val tags: List<String> = emptyList(),
    @ColumnInfo(dbWordTypeTag)
    val wordTypeTagId: Long? = null,
    @ColumnInfo(dbWordLearningProgress)
    val learningProgress: Float = 0f,
    @ColumnInfo(dbWordTranscription)
    val transcription: String = INVALID_TEXT,
    @ColumnInfo(dbWordExamples)
    val examples: List<String> = emptyList(),
)
