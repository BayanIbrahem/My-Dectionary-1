package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import kotlinx.coroutines.flow.Flow

interface LanguageRepo {
    suspend fun insertLanguages(languages: Collection<LanguageCode>): Boolean
    suspend fun insertLanguage(languageCode: LanguageCode): Boolean = insertLanguages(listOf(languageCode))
    suspend fun getLanguageWordSpace(code: LanguageCode): LanguageWordSpace?
    fun getAllLanguagesWordSpaces(includeNotUsedLanguages: Boolean): Flow<List<LanguageWordSpace>>
    suspend fun deleteWordSpace(languageCode: LanguageCode)
    fun getAllLanguages(): Flow<List<Language>>
}