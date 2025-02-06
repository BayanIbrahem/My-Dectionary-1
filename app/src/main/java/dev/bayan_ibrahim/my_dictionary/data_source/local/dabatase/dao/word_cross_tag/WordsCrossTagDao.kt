package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsWordId
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsCrossTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordCrossTag(wordCrossTag: WordCrossTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordCrossTagsList(wordCrossTag: Collection<WordCrossTagEntity>)

    @Delete
    suspend fun deleteWordsCrossTags(wordCrossTags: Collection<WordCrossTagEntity>)

    @Query(
        """
            DELETE FROM $dbWordsCrossTagsTable WHERE $dbWordsCrossTagsId IN (:ids)
        """
    )
    suspend fun deleteWordsCrossTagsOfIds(ids: Collection<Long>)

    @Query("""
        DELETE FROM $dbWordsCrossTagsTable WHERE $dbWordsCrossTagsWordId = :wordId
    """)
    suspend fun deleteWordsCrossTagsOfWord(wordId: Long)

    @Query(
        """
            SELECT * FROM $dbWordsCrossTagsTable
        """
    )
    fun getAllWordsCrossTags(): Flow<List<WordCrossTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossTagsTable WHERE $dbWordsCrossTagsWordId = :wordId
        """
    )
    fun getWordsCrossTagsOfWord(wordId: Long): Flow<List<WordCrossTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossTagsTable WHERE $dbWordsCrossTagsWordId IN (:wordsIds)
        """
    )
    fun getWordsCrossTagsOfWords(wordsIds: Collection<Long>): Flow<List<WordCrossTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossTagsTable WHERE $dbWordsCrossTagsTagId = :tagId
        """
    )
    fun getWordsCrossTagsOfTag(tagId: Long): Flow<List<WordCrossTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossTagsTable WHERE $dbWordsCrossTagsTagId IN (:tagsIds)
        """
    )
    fun getWordsCrossTagsOfTags(tagsIds: Collection<Long>): Flow<List<WordCrossTagEntity>>

    @Query(
        """
            SELECT * 
            FROM $dbWordsCrossTagsTable 
            WHERE 
                $dbWordsCrossTagsTagId IN (:tagsIds) 
                AND $dbWordsCrossTagsWordId IN (:wordsIds)
        """
    )
    fun getWordsCrossTagsOfWordsAndTags(
        wordsIds: Collection<Long>,
        tagsIds: Collection<Long>,
    ): Flow<List<WordCrossTagEntity>>
}