package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import kotlinx.coroutines.flow.Flow

interface MDWordSpaceRepo {
    suspend fun addNewWordSpace(languageCode: String): Boolean
    suspend fun getLanguagesWordSpacesWithTags(): Map<LanguageWordSpace, List<WordTypeTag>>
    fun getLanguagesWordSpaces(): Flow<List<LanguageWordSpace>>
    suspend fun editLanguageTags(code: LanguageCode, tags: List<WordTypeTag>)
}