package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table

import androidx.room.ColumnInfo
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceAverageLearningProgress
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceWordsCount
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.code

data class LanguageWordSpaceEntity(
    @ColumnInfo(dbLanguageWordSpaceLanguageCode)
    val languageCode: String,
    @ColumnInfo(dbLanguageWordSpaceWordsCount)
    val wordsCount: Int,
    @ColumnInfo(dbLanguageWordSpaceAverageLearningProgress)
    val averageLearningProgress: Float,
)
