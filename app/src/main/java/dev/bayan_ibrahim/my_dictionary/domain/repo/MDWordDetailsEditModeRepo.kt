package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.coroutines.flow.Flow

interface MDWordDetailsEditModeRepo: MDContextTagsRepo {
    suspend fun getWord(id: Long): Word?
    suspend fun saveNewWord(word: Word): Long
    suspend fun saveExistedWord(word: Word)
    fun getLanguageTagsStream(language: String): Flow<List<WordTypeTag>>
    suspend fun deleteWord(id: Long)
}