package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryDbNormalize
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.WordIdWithTagAndProgress
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLastTrain
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMemoryDecayFactor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTags
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTypeTag
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.flow.Flow


private const val getWordsWithRelatedQuery = """
            SELECT * 
            FROM $dbWordTable 
            WHERE  
                $dbWordId IN (:targetWords) AND
                $dbWordLanguageCode = :languageCode AND
                (
                    (
                        :includeMeaning AND $dbWordNormalizedMeaning LIKE :queryPattern
                    ) OR 
                    (
                        :includeTranslation AND $dbWordNormalizedTranslation LIKE :queryPattern
                    ) 
                )
        """

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
            SELECT $dbWordId FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsIdsOfLanguage(languageCode: String): Flow<List<Long>>

    @Query(
        """
            SELECT $dbWordId FROM $dbWordTable WHERE $dbWordTypeTag = :typeTag
        """
    )
    fun getWordsIdsOfTypeTag(typeTag: Long): Flow<List<Long>>

    @Query(
        """
            SELECT $dbWordId, $dbWordTags  FROM $dbWordTable
        """
    )
    fun getWordsIdsWithTags(): Flow<Map<@MapColumn(dbWordId) Long, @MapColumn(dbWordTags) String>>

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

    @Query(
        """
        SELECT
            $dbWordId,
            $dbWordTags,
            $dbWordMemoryDecayFactor
        FROM
            $dbWordTable
        WHERE
            (:includeEmptyTags OR LENGTH($dbWordTags) > 0) AND
            $dbWordMemoryDecayFactor BETWEEN :minProgress AND :maxProgress
    """
    )
    fun getWordsIdsWithTagsOfLearningProgressRange(
        includeEmptyTags: Boolean = true,
        minProgress: Float = 0f,
        maxProgress: Float = 1f,
    ): Flow<List<WordIdWithTagAndProgress>>


    fun getSortByColumnName(sortBy: MDWordsListViewPreferencesSortBy): String {
        return when (sortBy) {
            MDWordsListViewPreferencesSortBy.Meaning -> dbWordMeaning
            MDWordsListViewPreferencesSortBy.Translation -> dbWordTranslation
            MDWordsListViewPreferencesSortBy.LearningProgress -> dbWordMemoryDecayFactor
        }
    }

    fun getQueryPatternOfQuery(query: String): String {
        if (query.isEmpty()) return "%"
        return query.searchQueryDbNormalize
    }

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY :sortBy DESC")
    fun getPaginatedWordsWithRelatedDescOf(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
        sortBy: String,
    ): PagingSource<Int, WordWithRelatedWords>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY :sortBy ASC")
    fun getPaginatedWordsWithRelatedAscOf(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
        sortBy: String,
    ): PagingSource<Int, WordWithRelatedWords>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY :sortBy DESC")
    fun getWordsWithRelatedDescOf(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
        sortBy: String,
    ): Flow<List<WordWithRelatedWords>>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY :sortBy ASC")
    fun getWordsWithRelatedAscOf(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
        sortBy: String,
    ): Flow<List<WordWithRelatedWords>>

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
            SELECT $dbWordTags 
            FROM $dbWordTable 
        """
    )
    fun getAllTags(): Flow<List<String>>

}
