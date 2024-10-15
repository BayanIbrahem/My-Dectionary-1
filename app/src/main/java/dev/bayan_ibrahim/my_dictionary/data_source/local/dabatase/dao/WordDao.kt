package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsIdName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsTableName
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWord(word: Word)

    @Update
    fun updateWord(word: Word)

    @Delete
    fun deleteWord(vararg words: Word)

    @Delete
    fun deleteWord(words: Collection<Word>)

    @Query(
        """
            DELETE FROM $dbWordsTableName WHERE $dbWordsIdName IN (:ids)
        """
    )
    fun deleteWords(vararg ids: Long): Int

    @Query(
        """
            DELETE FROM $dbWordsTableName WHERE $dbWordsIdName IN (:ids)
        """
    )
    fun deleteWords(ids: Collection<Long>): Int

    @Query(
        """
        SELECT * FROM $dbWordsTableName WHERE $dbWordsIdName = :id
        """
    )
    fun getWord(id: Long): Word?

    @Query(
        """
        SELECT * FROM $dbWordsTableName WHERE $dbWordsIdName in (:ids)
        """
    )
    fun getWords(vararg ids: Long): Flow<List<Word>>

    @Query(
        """
        SELECT * FROM $dbWordsTableName WHERE $dbWordsIdName in (:ids)
        """
    )
    fun getWords(ids: Collection<Long>): Flow<List<Word>>

    @Query(
        """
            SELECT * FROM $dbWordsTableName
        """
    )
    fun getAllWords(): Flow<List<Word>>
}
