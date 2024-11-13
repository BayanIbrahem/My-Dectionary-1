package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceAverageLearningProgress
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceWordsCount
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLearningProgress
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTags
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelatedWords(words: Collection<WordTypeTagRelatedWordEntity>)

    @Transaction
    suspend fun insertWordWithRelations(word: WordEntity, relatedWords: List<WordTypeTagRelatedWordEntity>): Long {
        val wordId = insertWord(word)
        val relatedWordsWithWordIdAndNullId = relatedWords.map { it.copy(baseWordId = wordId, id = null) }
        insertRelatedWords(relatedWordsWithWordIdAndNullId)
        return wordId
    }

    @Update
    suspend fun updateWord(word: WordEntity)

    @Transaction
    suspend fun updateWordWithRelations(word: WordEntity, relatedWords: List<WordTypeTagRelatedWordEntity>) {
        updateWord(word)
        deleteRelatedWordsOf(word.id!!)
        val relatedWordsWithNullId = relatedWords.map { it.copy(id = null) }
        insertRelatedWords(relatedWordsWithNullId)
    }

    @Delete
    suspend fun deleteWord(vararg words: WordEntity)

    @Delete
    suspend fun deleteWord(words: Collection<WordEntity>)

    @Query(
        """
            DELETE FROM $dbTypeTagRelatedWordTable WHERE $dbTypeTagRelatedWordBaseWordId = :wordId
        """
    )
    suspend fun deleteRelatedWordsOf(wordId: Long)

    @Query(
        """
            DELETE FROM $dbWordTable WHERE $dbWordId IN (:ids)
        """
    )
    suspend fun deleteWords(vararg ids: Long): Int

    @Query(
        """
            DELETE FROM $dbWordTable WHERE $dbWordId IN (:ids)
        """
    )
    suspend fun deleteWords(ids: Collection<Long>): Int

    @Query(
        """
        SELECT * FROM $dbWordTable WHERE $dbWordId = :id
        """
    )
    suspend fun getWord(id: Long): WordEntity?

    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWords(vararg ids: Long): Flow<List<WordEntity>>

    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWords(ids: Collection<Long>): Flow<List<WordEntity>>

    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsOfLanguage(languageCode: String): Flow<List<WordEntity>>

    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWords(): Flow<List<WordEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId = :id
        """
    )
    suspend fun getWordWithRelatedWords(id: Long): WordWithRelatedWords?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithRelatedWords(vararg ids: Long): Flow<List<WordWithRelatedWords>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithRelatedWords(ids: Collection<Long>): Flow<List<WordWithRelatedWords>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsWithRelatedOfLanguage(languageCode: String): Flow<List<WordWithRelatedWords>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWordsWithRelatedWords(): Flow<List<WordWithRelatedWords>>

    @Query(
        """
            SELECT $dbWordTags 
            FROM $dbWordTable 
            WHERE $dbWordLanguageCode = :code
        """
    )
    fun getTagsInLanguage(code: String): Flow<List<String>>

    @Query(
        """
            SELECT 
                $dbWordLanguageCode as $dbLanguageWordSpaceLanguageCode, 
                COUNT(*) as $dbLanguageWordSpaceWordsCount, 
                AVG($dbWordLearningProgress) $dbLanguageWordSpaceAverageLearningProgress
            FROM $dbWordTable
            GROUP BY $dbWordLanguageCode
            ORDER BY $dbLanguageWordSpaceWordsCount DESC
    """
    )
    fun getLanguagesWordSpaces(): Flow<List<LanguageWordSpaceEntity>>

    @Query(
        """
            SELECT 
                $dbWordLanguageCode as $dbLanguageWordSpaceLanguageCode, 
                COUNT(*) as $dbLanguageWordSpaceWordsCount, 
                AVG($dbWordLearningProgress) $dbLanguageWordSpaceAverageLearningProgress
            FROM $dbWordTable
            WHERE $dbWordLanguageCode = :languageCode
            GROUP BY $dbWordLanguageCode
            ORDER BY $dbLanguageWordSpaceWordsCount DESC
            LIMIT 1
    """
    )
    suspend fun getLanguagesWordSpace(languageCode: String): LanguageWordSpaceEntity?

    @Query(
        """
            SELECT * 
            FROM $dbWordTable
        """
    )
    fun e()

}
