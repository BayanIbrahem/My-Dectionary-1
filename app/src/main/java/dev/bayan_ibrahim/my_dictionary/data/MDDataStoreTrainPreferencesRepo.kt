package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainPreferencesRepo
import kotlinx.coroutines.flow.Flow

class MDDataStoreTrainPreferencesRepo(
    private val dataStore: MDPreferencesDataStore,
) : TrainPreferencesRepo {
    override suspend fun setTrainPreferences(preferences: MDWordsListTrainPreferences) {
        dataStore.writeWordsListTrainPreferences { preferences }
    }

    override fun getTrainPreferencesStream(): Flow<MDWordsListTrainPreferences> = dataStore.getWordsListTrainPreferencesStream()
}