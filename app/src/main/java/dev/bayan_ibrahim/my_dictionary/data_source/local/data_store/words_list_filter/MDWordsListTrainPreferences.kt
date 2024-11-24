package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.copy
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferencesBuilder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MDWordsListTrainPreferences {
    fun getWordsListTrainPreferencesStream(): Flow<WordsListTrainPreferences>
    suspend fun getWordsListTrainPreferences() = getWordsListTrainPreferencesStream().first()
    suspend fun writeWordsListTrainPreferences(getPreferences: (WordsListTrainPreferences) -> WordsListTrainPreferences)
}

class MDWordsListTrainPreferencesImpl(
    context: Context,
) : MDWordsListTrainPreferences {
    private val proto = context.wordsListTrainPreferencesDataStore
    override fun getWordsListTrainPreferencesStream(): Flow<WordsListTrainPreferences> = proto.data.map {
        WordsListTrainPreferencesBuilder(
            trainType = WordsListTrainType.entries[it.trainType],
            trainTarget = WordsListTrainTarget.entries[it.trainTarget],
            limit = WordsListTrainPreferencesLimit.entries[it.limit],
            sortBy = WordsListTrainPreferencesSortBy.entries[it.sortBy],
            sortByOrder = WordsListSortByOrder.entries[it.sortByOrder],
        )
    }

    override suspend fun writeWordsListTrainPreferences(getPreferences: (WordsListTrainPreferences) -> WordsListTrainPreferences) {
        proto.updateData {
            it.copy {
                val preferences = getPreferences(getWordsListTrainPreferences())
                trainType = preferences.trainType.ordinal
                trainTarget = preferences.trainTarget.ordinal
                limit = preferences.limit.ordinal
                sortBy = preferences.sortBy.ordinal
                sortByOrder = preferences.sortByOrder.ordinal
            }
        }
    }
}