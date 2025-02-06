package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.coroutines.flow.Flow

interface WordClassRepo {
    fun getWordsClassesOfLanguage(code: LanguageCode): Flow<List<WordClass>>
    fun getAllWordsClasses(): Flow<Map<LanguageCode, List<WordClass>>>
    suspend fun getWordClass(id: Long): WordClass?
    suspend fun setLanguageWordsClasses(code: LanguageCode, tags: List<WordClass>)
}