package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import kotlinx.coroutines.flow.Flow

class MDDataStoreUserPreferencesRepo(
    private val dataStore: MDPreferencesDataStore,
) : UserPreferencesRepo {
    override suspend fun setUserPreferences(builder: (MDUserPreferences) -> MDUserPreferences) {
        dataStore.writeUserPreferences(builder)
    }

    override fun getUserPreferencesStream(): Flow<MDUserPreferences> = dataStore.getUserPreferencesStream()
}