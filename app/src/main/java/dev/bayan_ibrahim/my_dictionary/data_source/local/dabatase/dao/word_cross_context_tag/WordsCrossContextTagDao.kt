package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsWordId
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsCrossContextTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordCrossContextTag(wordCrossTag: WordCrossContextTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordCrossContextTagsList(wordCrossTag: Collection<WordCrossContextTagEntity>)

    @Delete
    suspend fun deleteWordsCrossContextTags(wordCrossTags: Collection<WordCrossContextTagEntity>)

    @Query(
        """
            DELETE FROM $dbWordsCrossContextTagsTable WHERE $dbWordsCrossContextTagsId IN (:ids)
        """
    )
    suspend fun deleteWordsCrossContextTagsOfIds(ids: Collection<Long>)

    @Query("""
        DELETE FROM $dbWordsCrossContextTagsTable WHERE $dbWordsCrossContextTagsWordId = :wordId
    """)
    suspend fun deleteWordsCrossContextTagsOfWord(wordId: Long)

    @Query(
        """
            SELECT * FROM $dbWordsCrossContextTagsTable
        """
    )
    fun getAllWordsCrossContextTags(): Flow<List<WordCrossContextTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossContextTagsTable WHERE $dbWordsCrossContextTagsWordId = :wordId
        """
    )
    fun getWordsCrossContextTagsOfWord(wordId: Long): Flow<List<WordCrossContextTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossContextTagsTable WHERE $dbWordsCrossContextTagsWordId IN (:wordsIds)
        """
    )
    fun getWordsCrossContextTagsOfWords(wordsIds: Collection<Long>): Flow<List<WordCrossContextTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossContextTagsTable WHERE $dbWordsCrossContextTagsTagId = :tagId
        """
    )
    fun getWordsCrossContextTagsOfTag(tagId: Long): Flow<List<WordCrossContextTagEntity>>

    @Query(
        """
            SELECT * FROM $dbWordsCrossContextTagsTable WHERE $dbWordsCrossContextTagsTagId IN (:tagsIds)
        """
    )
    fun getWordsCrossContextTagsOfTags(tagsIds: Collection<Long>): Flow<List<WordCrossContextTagEntity>>

    @Query(
        """
            SELECT * 
            FROM $dbWordsCrossContextTagsTable 
            WHERE 
                $dbWordsCrossContextTagsTagId IN (:tagsIds) 
                AND $dbWordsCrossContextTagsWordId IN (:wordsIds)
        """
    )
    fun getWordsCrossContextTagsOfWordsAndTags(
        wordsIds: Collection<Long>,
        tagsIds: Collection<Long>,
    ): Flow<List<WordCrossContextTagEntity>>
}