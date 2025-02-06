package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryDbNormalize
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLastTrain
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMemoryDecayFactor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordWordClass
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.flow.Flow


// TODO, split get word with related words to 3 groups, only related, only tags, tags and related
@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity): Long

    // even this function is duplicated but is more maintainable to keep two versions of it since it is being used here in another function
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelatedWords(words: Collection<WordWordClassRelatedWordEntity>)

    @Transaction
    suspend fun insertWordWithRelations(
        word: WordEntity,
        relatedWords: List<WordWordClassRelatedWordEntity>,
    ): Long {
        val wordId = insertWord(word)
        val relatedWordsWithWordIdAndNullId = relatedWords.map { it.copy(baseWordId = wordId, id = null) }
        insertRelatedWords(relatedWordsWithWordIdAndNullId)
        return wordId
    }

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query(
        """
            UPDATE $dbWordTable 
            SET 
                $dbWordLastTrain = :lastTrainTime, 
                $dbWordMemoryDecayFactor = :newDecay
            WHERE $dbWordId  = :id
        """
    )
    suspend fun updateLastTrainHistoryAndMemoryDecay(id: Long, lastTrainTime: Long, newDecay: Float)

    @Transaction
    suspend fun updateWordWithRelations(
        word: WordEntity,
        relatedWords: List<WordWordClassRelatedWordEntity>,
    ) {
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
            DELETE FROM $dbWordClassRelatedWordTable WHERE $dbWordClassRelatedWordBaseWordId = :wordId
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
        SELECT * 
        FROM $dbWordTable 
        WHERE 
            $dbWordMeaning = :meaning AND 
            $dbWordTranslation = :translation AND 
            $dbWordLanguageCode = :languageCode
        """
    )
    suspend fun getWord(
        meaning: String,
        translation: String,
        languageCode: String,
    ): WordEntity?

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
            SELECT $dbWordId FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsIdsOfLanguage(languageCode: String): Flow<List<Long>>

    @Query(
        """
            SELECT $dbWordId FROM $dbWordTable WHERE $dbWordWordClass = :wordClass
        """
    )
    fun getWordsIdsOfWordClass(wordClass: Long): Flow<List<Long>>

    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWords(): Flow<List<WordEntity>>

    @Query(
        """
            SELECT * FROM $dbWordTable
            WHERE (
                (NOT :includeLanguage) OR $dbWordLanguageCode IN (:languages)
            ) AND (
                (NOT :includeWordClass) OR $dbWordWordClass IN (:wordsClasses)
            )
        """
    )
    fun getWordsOf(
        includeLanguage: Boolean,
        languages: Set<String>,

        includeWordClass: Boolean,
        wordsClasses: Set<Long>,
    ): Flow<List<WordEntity>>

    fun getQueryPatternOfQuery(query: String): String {
        if (query.isEmpty()) return "%"
        return query.searchQueryDbNormalize
    }
}
