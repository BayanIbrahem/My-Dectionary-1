package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.coroutines.flow.Flow

interface WordClassRepo {
    fun getWordsClassesOfLanguage(code: LanguageCode): Flow<List<WordClass>>
    fun getAllWordsClasses(): Flow<Map<LanguageCode, List<WordClass>>>
    suspend fun getWordClass(id: Long): WordClass?
    suspend fun getWordClass(name: String): WordClass?
    suspend fun getWordClassRelation(id: Long): WordClassRelation?
    suspend fun getWordClassRelation(wordClassId: Long, label: String): WordClassRelation?
    suspend fun setLanguageWordsClasses(code: LanguageCode, wordsClasses: List<WordClass>)

    /**
     * add word class content to the language and return its id.
     * if the word id is not invalid, we only add the relations
     * if the word class id is invalid we check by word class name if it is existed in the database or not
     * and according to that we decide if we add the word class or not
     */
    suspend fun addWordClass(wordClass: WordClass): WordClass
    suspend fun addWordClassRelation(wordClassId:Long, label: String): WordClassRelation
}