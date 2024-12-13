package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface MDWordsListTrainDialogRepo {
    suspend fun setTrainPreferences(preferences: MDWordsListTrainPreferences)
    fun getTrainPreferencesStream(): Flow<MDWordsListTrainPreferences>
    suspend fun getTrainPreferences() = getTrainPreferencesStream().first()
}
