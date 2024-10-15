package dev.bayan_ibrahim.my_dictionary.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsAdditionalTranslationsName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsExamplesName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsIdName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsLanguageCodeName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsLearningProgressName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsMeaningName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsTableName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsTagsName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsTranscriptionName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsTranslationName

@Entity(tableName = dbWordsTableName)
@TypeConverters(StringListConverter::class)
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbWordsIdName)
    val id: Long? = null,
    @ColumnInfo(dbWordsMeaningName)
    val meaning: String,
    @ColumnInfo(dbWordsTranslationName)
    val translation: String,
    @ColumnInfo(dbWordsLanguageCodeName)
    val languageCode: String,
    @ColumnInfo(dbWordsAdditionalTranslationsName)
    val additionalTranslations: List<String> = emptyList(),
    @ColumnInfo(dbWordsTagsName)
    val tags: List<String> = emptyList(),
    @ColumnInfo(dbWordsLearningProgressName)
    val learningProgress: Float = 0f,
    @ColumnInfo(dbWordsTranscriptionName)
    val transcription: String = "",
    @ColumnInfo(dbWordsExamplesName)
    val examples: List<String> = emptyList(),
)
