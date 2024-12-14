package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListViewPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListViewPreferencesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MDWordsListViewPreferencesRepoImpl(
    private val wordDao: WordDao,
    private val dataStore: MDWordsListViewPreferencesDataStore,
    userPreferences: MDUserPreferencesDataStore,
) : MDWordsListViewPreferencesRepo {
    private val languageFlow = userPreferences.getUserPreferencesStream().map {
        it.selectedLanguagePage
    }

    override fun getViewPreferencesStream(): Flow<MDWordsListViewPreferences> {
        return dataStore.getWordsListViewPreferencesStream()
    }

    override suspend fun setViewPreferences(preferences: MDWordsListViewPreferences) {
        dataStore.writeWordsListViewPreferences { preferences }
    }

    override suspend fun getSelectedLanguageTags(): Set<String> {
        val currentLanguage = languageFlow.firstOrNull() ?: return emptySet()
        return wordDao.getTagsInLanguage(currentLanguage.code.code).map {
            it.map(StringListConverter::stringToListConverter).flatten().toSet()
        }.first()
    }
}