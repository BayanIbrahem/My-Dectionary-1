package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface ViewPreferencesRepo {
    fun getViewPreferencesStream(): Flow<MDWordsListViewPreferences>
    suspend fun getViewPreferences(): MDWordsListViewPreferences = getViewPreferencesStream().first()
    suspend fun setViewPreferences(preferences: MDWordsListViewPreferences)
    suspend fun setViewPreferences(buildPreferences: (MDWordsListViewPreferences) -> MDWordsListViewPreferences)
}