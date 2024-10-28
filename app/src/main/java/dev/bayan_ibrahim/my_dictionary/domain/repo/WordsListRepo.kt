package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import kotlinx.coroutines.flow.Flow

interface WordsListRepo {
    // view preferences
    fun getViewPreferences(): Flow<WordsListViewPreferences>
    suspend fun setViewPreferences(preferences: WordsListViewPreferences)
    suspend fun setSelectedLanguagePage(code: String)
    suspend fun getSelectedLanguagePage(): Language?
    fun getSelectedLanguagePageStream(): Flow<Language?>
    fun getLanguageTags(languageCode: String): Flow<Set<String>>

    // words list
    fun getWordsList(languageCode: String, viewPreferences: WordsListViewPreferences): Flow<List<Word>>
    suspend fun deleteWords(ids: Collection<Long>)

    // WordSpaces
    fun getAllLanguagesWordSpaces(): Flow<List<LanguageWordSpace>>
}