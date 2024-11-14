package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import kotlinx.coroutines.flow.Flow

interface MDWordsListRepo {
    // view preferences
    fun getViewPreferences(): Flow<WordsListViewPreferences>
    suspend fun setViewPreferences(preferences: WordsListViewPreferences)
    suspend fun setSelectedLanguagePage(code: LanguageCode)
    suspend fun getSelectedLanguagePage(): Language?
    fun getSelectedLanguagePageStream(): Flow<Language?>
    fun getLanguageTags(code: LanguageCode): Flow<Set<String>>

    // words list
    fun getWordsList(code: LanguageCode, viewPreferences: WordsListViewPreferences): Flow<List<Word>>
    suspend fun deleteWords(ids: Collection<Long>)

    // WordSpaces
    fun getAllLanguagesWordSpaces(includeNotUsedLanguages: Boolean = true): Flow<List<LanguageWordSpace>>
    suspend fun getLanguagesWordSpaces(code: LanguageCode): LanguageWordSpace?
}