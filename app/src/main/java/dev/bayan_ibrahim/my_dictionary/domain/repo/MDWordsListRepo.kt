package dev.bayan_ibrahim.my_dictionary.domain.repo

import androidx.paging.PagingData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.coroutines.flow.Flow


interface MDWordsListRepo : MDTrainPreferencesRepo, MDLanguageSelectionDialogRepo {
    // view preferences
    fun getViewPreferences(): Flow<MDWordsListViewPreferences>
    fun getUserPreferences(): Flow<MDUserPreferences>
    fun getLanguageTags(code: LanguageCode): Flow<Set<String>>

    // words list
    fun getWordsList(
        code: LanguageCode,
        viewPreferences: MDWordsListViewPreferences,
    ): Flow<List<Word>>

    // words list
    fun getPaginatedWordsList(
        code: LanguageCode,
        wordsIdsOfTagsAndProgressRange: Set<Long>,
        viewPreferences: MDWordsListViewPreferences,
    ): Flow<PagingData<Word>>

    suspend fun deleteWords(ids: Collection<Long>)
}