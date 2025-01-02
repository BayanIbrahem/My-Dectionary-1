package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.coroutines.flow.Flow

interface TypeTagRepo {
    fun getTypeTagsOfLanguage(code: LanguageCode): Flow<List<WordTypeTag>>
    fun getAllTypeTags(): Flow<Map<LanguageCode, List<WordTypeTag>>>
    suspend fun getTypeTag(id: Long): WordTypeTag?
    suspend fun setLanguageTypeTags(code: LanguageCode, tags: List<WordTypeTag>)
}