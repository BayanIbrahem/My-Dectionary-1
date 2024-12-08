package dev.bayan_ibrahim.my_dictionary.domain.repo

import androidx.paging.PagingData
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import kotlinx.coroutines.flow.Flow

interface MDWordsListRepo {
    // view preferences
    fun getViewPreferences(): Flow<WordsListViewPreferences>
    suspend fun setViewPreferences(preferences: WordsListViewPreferences)
    suspend fun setTrainPreferences(preferences: WordsListTrainPreferences)
    suspend fun setSelectedLanguagePage(code: LanguageCode)
    suspend fun getSelectedLanguagePage(): Language?
    fun getSelectedLanguagePageStream(): Flow<Language?>
    fun getLanguageTags(code: LanguageCode): Flow<Set<String>>

    // words list
    fun getWordsList(
        code: LanguageCode,
        viewPreferences: WordsListViewPreferences,
    ): Flow<List<Word>>

    // words list
    fun getPaginatedWordsList(
        code: LanguageCode,
        wordsIdsOfTagsAndProgressRange: Set<Long>,
        viewPreferences: WordsListViewPreferences,
    ): Flow<PagingData<Word>>

    suspend fun deleteWords(ids: Collection<Long>)

    // WordSpaces
    fun getAllLanguagesWordSpaces(includeNotUsedLanguages: Boolean = true): Flow<List<LanguageWordSpace>>
    suspend fun getLanguagesWordSpaces(code: LanguageCode): LanguageWordSpace?
    suspend fun getWordsIdsOfTagsAndProgressRange(viewPreferences: WordsListViewPreferences): Set<Long>
}