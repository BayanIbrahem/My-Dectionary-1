package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import kotlinx.coroutines.flow.Flow

interface WordDetailsRepo {
    suspend fun getWord(wordId: Long): Word
    suspend fun saveNewWord(word: Word): Word
    suspend fun saveExistedWord(word: Word)
    suspend fun getLanguageTags(language: String): List<WordTypeTag>
    fun getLanguageTagsStream(language: String): Flow<List<WordTypeTag>>
    suspend fun deleteWord(id: Long)
}