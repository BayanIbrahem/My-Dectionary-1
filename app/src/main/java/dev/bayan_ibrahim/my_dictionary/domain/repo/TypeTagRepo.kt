package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.WordWordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.coroutines.flow.Flow

interface WordClassRepo {
    fun getWordsClassesOfLanguage(code: LanguageCode): Flow<List<WordWordClass>>
    fun getAllWordsClasses(): Flow<Map<LanguageCode, List<WordWordClass>>>
    suspend fun getWordClass(id: Long): WordWordClass?
    suspend fun setLanguageWordsClasses(code: LanguageCode, tags: List<WordWordClass>)
}