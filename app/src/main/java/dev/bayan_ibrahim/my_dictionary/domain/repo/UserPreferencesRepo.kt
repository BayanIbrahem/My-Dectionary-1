package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface UserPreferencesRepo {
    suspend fun setUserPreferences(preferences: MDUserPreferences) = setUserPreferences { preferences }
    suspend fun setUserPreferences(builder: (MDUserPreferences) -> MDUserPreferences)
    fun getUserPreferencesStream(): Flow<MDUserPreferences>
    suspend fun getUserPreferences() = getUserPreferencesStream().first()
}