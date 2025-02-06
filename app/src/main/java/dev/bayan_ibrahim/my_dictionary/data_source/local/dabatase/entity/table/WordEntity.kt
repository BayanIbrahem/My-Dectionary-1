package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordAdditionalTranslations
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordAntonym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordCollocation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordCreatedAt
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordExamples
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordHolonym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordHomograph
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordHomonym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordHomophone
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordHypernym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordHyponym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLastTrain
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMemoryDecayFactor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMeronym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMetonymy
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordPolysemy
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordPrototype
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordSynonym
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranscription
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClass
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNote
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordUpdatedAt

@Entity(
    tableName = dbWordTable,
    foreignKeys = [
        ForeignKey(
            entity = WordClassEntity::class,
            parentColumns = [dbWordClassId],
            childColumns = [dbWordClass],
            onUpdate = ForeignKey.SET_NULL,
            onDelete = ForeignKey.SET_NULL,
        ),

        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = [dbLanguageCode],
            childColumns = [dbWordLanguageCode],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(dbWordClass),
        Index(dbWordLanguageCode),
        Index(dbWordNormalizedMeaning),
        Index(dbWordNormalizedTranslation),
    ]
)
@TypeConverters(StringListConverter::class)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(dbWordId)
    val id: Long? = null,
    @ColumnInfo(dbWordMeaning)
    val meaning: String,
    @ColumnInfo(dbWordCreatedAt)
    val createdAt: Long,
    @ColumnInfo(dbWordUpdatedAt)
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(dbWordNormalizedMeaning)
    val normalizedMeaning: String = meaning.meaningSearchNormalize,
    @ColumnInfo(dbWordTranslation)
    val translation: String,
    @ColumnInfo(dbWordNormalizedTranslation)
    val normalizedTranslation: String = translation.meaningSearchNormalize,
    @ColumnInfo(dbWordLanguageCode)
    val languageCode: String,
    @ColumnInfo(dbWordAdditionalTranslations)
    val additionalTranslations: List<String> = emptyList(),
    @ColumnInfo(dbWordClass)
    val wordClassId: Long? = null,
    @ColumnInfo(dbWordMemoryDecayFactor, defaultValue = "1")
    val memoryDecayFactor: Float = 1f,
    @ColumnInfo(dbWordLastTrain, defaultValue = "NULL")
    val lastTrainTime: Long? = null,
    @ColumnInfo(dbWordTranscription)
    val transcription: String = INVALID_TEXT,
    @ColumnInfo(dbWordNote, defaultValue = INVALID_TEXT)
    val note: String = INVALID_TEXT,
    @ColumnInfo(dbWordExamples)
    val examples: List<String> = emptyList(),
    @ColumnInfo(dbWordSynonym)
    val synonym: List<String> = emptyList(),
    @ColumnInfo(dbWordAntonym)
    val antonym: List<String> = emptyList(),
    @ColumnInfo(dbWordHyponym)
    val hyponym: List<String> = emptyList(),
    @ColumnInfo(dbWordHypernym)
    val hypernym: List<String> = emptyList(),
    @ColumnInfo(dbWordMeronym)
    val meronym: List<String> = emptyList(),
    @ColumnInfo(dbWordHolonym)
    val holonym: List<String> = emptyList(),
    @ColumnInfo(dbWordHomonym)
    val homonym: List<String> = emptyList(),
    @ColumnInfo(dbWordPolysemy)
    val polysemy: List<String> = emptyList(),
    @ColumnInfo(dbWordPrototype)
    val prototype: List<String> = emptyList(),
    @ColumnInfo(dbWordMetonymy)
    val metonymy: List<String> = emptyList(),
    @ColumnInfo(dbWordCollocation)
    val collocation: List<String> = emptyList(),
    @ColumnInfo(dbWordHomograph)
    val homograph: List<String> = emptyList(),
    @ColumnInfo(dbWordHomophone)
    val homophone: List<String> = emptyList(),
)
