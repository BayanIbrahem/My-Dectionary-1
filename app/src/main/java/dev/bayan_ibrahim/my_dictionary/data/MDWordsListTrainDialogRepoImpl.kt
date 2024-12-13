package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListTrainPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListTrainDialogRepo
import kotlinx.coroutines.flow.Flow

class MDWordsListTrainDialogRepoImpl(
    private val dataStore: MDWordsListTrainPreferencesDataStore
): MDWordsListTrainDialogRepo {
    override suspend fun setTrainPreferences(preferences: MDWordsListTrainPreferences) {
        dataStore.writeWordsListTrainPreferences {
            preferences
        }
    }

    override fun getTrainPreferencesStream(): Flow<MDWordsListTrainPreferences> = dataStore.getWordsListTrainPreferencesStream()
}