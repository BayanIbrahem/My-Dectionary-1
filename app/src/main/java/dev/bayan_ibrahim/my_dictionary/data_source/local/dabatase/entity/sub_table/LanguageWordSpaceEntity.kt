package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table

import androidx.room.ColumnInfo
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceAverageMemorizingProbability
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceWordsCount

data class LanguageWordSpaceEntity(
    @ColumnInfo(dbLanguageWordSpaceLanguageCode)
    val languageCode: String,
    @ColumnInfo(dbLanguageWordSpaceWordsCount)
    val wordsCount: Int,
    @ColumnInfo(dbLanguageWordSpaceAverageMemorizingProbability)
    val averageMemorizingProbability: Float,
)
