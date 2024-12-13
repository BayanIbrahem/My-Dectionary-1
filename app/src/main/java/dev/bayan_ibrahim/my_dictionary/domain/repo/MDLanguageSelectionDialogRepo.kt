package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import kotlinx.coroutines.flow.Flow

interface MDLanguageSelectionDialogRepo {
    suspend fun setSelectedLanguagePage(code: LanguageCode)
    suspend fun getSelectedLanguagePage(): Language?
    suspend fun getLanguagesWordSpaces(code: LanguageCode): LanguageWordSpace?
    fun getSelectedLanguagePageStream(): Flow<Language?>
    fun getAllLanguagesWordSpaces(includeNotUsedLanguages: Boolean = true): Flow<List<LanguageWordSpace>>
}