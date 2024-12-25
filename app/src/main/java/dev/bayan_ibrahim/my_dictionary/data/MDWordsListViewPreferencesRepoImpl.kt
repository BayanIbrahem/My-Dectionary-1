package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListViewPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListViewPreferencesRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class MDWordsListViewPreferencesRepoImpl(
    private val wordDao: WordDao,
    private val wordWithTagsDao: WordWithContextTagDao,
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

    // TODO, this function is duplicated with similar one in words list repo
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getSelectedLanguageTags(): Flow<Set<ContextTag>> = languageFlow.mapNotNull {
        val languageCode = it?.code?.code ?: return@mapNotNull null
        wordDao.getWordsIdsOfLanguage(
            languageCode = languageCode
        ).flatMapConcat { wordsIds ->
            wordWithTagsDao.getWordsWithTagsRelations(wordsIds)
        }.map {
            it.map {
                it.tags.map { it.asModel() }
            }.flatten().toSet()
        }.first()
    }
}