package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.ViewPreferencesRepo
import kotlinx.coroutines.flow.Flow

class MDDataStoreViewPreferencesRepo(
    private val dataStore: MDPreferencesDataStore,
) : ViewPreferencesRepo {
    override suspend fun setViewPreferences(preferences: MDWordsListViewPreferences) {
        dataStore.writeWordsListViewPreferences { preferences }
    }

    override fun getViewPreferencesStream(): Flow<MDWordsListViewPreferences> = dataStore.getWordsListViewPreferencesStream()
}