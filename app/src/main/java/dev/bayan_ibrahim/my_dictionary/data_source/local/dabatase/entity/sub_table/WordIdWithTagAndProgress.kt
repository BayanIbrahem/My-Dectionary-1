package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table

import androidx.room.ColumnInfo
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLearningProgress
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTags

data class WordIdWithTagAndProgress(
    @ColumnInfo(dbWordId)
    val wordId: Long,
    @ColumnInfo(dbWordTags)
    val tags: String,
    @ColumnInfo(dbWordLearningProgress)
    val progress: Float,
)
