package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.coroutines.flow.Flow

interface MDTrainRepo: MDTrainPreferencesRepo {
    suspend fun getTrainPreferences(): MDWordsListTrainPreferences
    suspend fun getViewPreferences(): WordsListViewPreferences
    suspend fun getAllSelectedLanguageWords(): Sequence<Word>
    fun getTrainHistoryOf(
        timeRange: LongRange? = null,
        trainTypes: Set<TrainWordType> = emptySet(),
        excludeSpecifiedWordsIds: Boolean = false,
        wordsIds: Set<Long> = emptySet()
    ): Flow<List<TrainHistory>>

    suspend fun submitTrainHistory(trainHistory: TrainHistory)
}