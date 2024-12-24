package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.coroutines.flow.Flow

interface MDWordDetailsRepo {
    suspend fun getWord(wordId: Long): Word
    suspend fun saveNewWord(word: Word): Word
    suspend fun saveExistedWord(word: Word)
    fun getContextTagsStream(): Flow<List<ContextTag>>
    suspend fun addOrUpdateContextTag(tag: ContextTag): ContextTag
    suspend fun getLanguageTags(language: String): List<WordTypeTag>
    fun getLanguageTagsStream(language: String): Flow<List<WordTypeTag>>
    suspend fun deleteWord(id: Long)
}