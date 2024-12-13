package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.copy
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferencesBuilder
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

interface MDWordsListTrainPreferencesDataStore {
    fun getWordsListTrainPreferencesStream(): Flow<MDWordsListTrainPreferences>
    suspend fun getWordsListTrainPreferences() = getWordsListTrainPreferencesStream().firstOrNull() ?: defaultWordsListTrainPreferences
    suspend fun writeWordsListTrainPreferences(getPreferences: (MDWordsListTrainPreferences) -> MDWordsListTrainPreferences)
}

class MDWordsListDataStoreTrainPreferencesImpl(
    context: Context,
) : MDWordsListTrainPreferencesDataStore {
    private val proto = context.wordsListTrainPreferencesDataStore
    override fun getWordsListTrainPreferencesStream(): Flow<MDWordsListTrainPreferences> = proto.data.map {
        MDWordsListTrainPreferencesBuilder(
            trainType = TrainWordType.entries[it.trainType],
            trainTarget = WordsListTrainTarget.entries[it.trainTarget],
            limit = WordsListTrainPreferencesLimit.entries[it.limit],
            sortBy = WordsListTrainPreferencesSortBy.entries[it.sortBy],
            sortByOrder = MDWordsListSortByOrder.entries[it.sortByOrder],
        )
    }

    override suspend fun writeWordsListTrainPreferences(getPreferences: (MDWordsListTrainPreferences) -> MDWordsListTrainPreferences) {
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