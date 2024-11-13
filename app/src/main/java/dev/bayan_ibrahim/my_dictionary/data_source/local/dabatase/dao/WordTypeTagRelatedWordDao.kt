package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordTypeTagRelatedWordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelatedWord(word: WordTypeTagRelatedWordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelatedWords(words: List<WordTypeTagRelatedWordEntity>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateRelatedWord(word: WordTypeTagRelatedWordEntity)

    @Delete
    suspend fun deleteRelatedWord(word: WordTypeTagRelatedWordEntity)

    @Delete
    suspend fun deleteRelatedWords(words: List<WordTypeTagRelatedWordEntity>)

    @Query(
        """
            DELETE FROM $dbTypeTagRelatedWordTable WHERE $dbTypeTagRelatedWordId = :id
        """
    )
    suspend fun deleteRelatedWord(id: Long)

    @Query(
        """
            DELETE FROM $dbTypeTagRelatedWordTable WHERE $dbTypeTagRelatedWordId IN (:ids)
        """
    )
    suspend fun deleteRelatedWords(ids: List<Long>)

    @Query(
        """
            SELECT * FROM $dbTypeTagRelatedWordTable 
        """
    )
    fun getAllRelatedWords(): Flow<List<WordTypeTagRelatedWordEntity>>

    @Query(
        """
            SELECT 
                $dbTypeTagRelatedWordRelationId as id,
                COUNT(*) as count
            FROM $dbTypeTagRelatedWordTable 
            GROUP BY $dbTypeTagRelatedWordRelationId
        """
    )
    fun getRelatedWordsCount(): Map<@MapColumn("id") Long, @MapColumn("count") Int>
}