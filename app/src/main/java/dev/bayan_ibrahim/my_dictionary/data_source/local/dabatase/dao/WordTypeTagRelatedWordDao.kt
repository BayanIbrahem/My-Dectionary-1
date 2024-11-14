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
abstract class WordTypeTagRelatedWordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertRelatedWord(word: WordTypeTagRelatedWordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertRelatedWords(words: List<WordTypeTagRelatedWordEntity>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun updateRelatedWord(word: WordTypeTagRelatedWordEntity)

    @Delete
    abstract suspend fun deleteRelatedWord(word: WordTypeTagRelatedWordEntity)

    @Delete
    abstract suspend fun deleteRelatedWords1(words: List<WordTypeTagRelatedWordEntity>)

    @Query(
        """
            DELETE FROM $dbTypeTagRelatedWordTable WHERE $dbTypeTagRelatedWordId = :id
        """
    )
    abstract suspend fun deleteRelatedWord(id: Long)

    @Query(
        """
            DELETE FROM $dbTypeTagRelatedWordTable WHERE $dbTypeTagRelatedWordId IN (:ids)
        """
    )
    abstract suspend fun deleteRelatedWords2(ids: List<Long>)

    @Query(
        """
            SELECT * FROM $dbTypeTagRelatedWordTable 
        """
    )
    abstract fun getAllRelatedWords(): Flow<List<WordTypeTagRelatedWordEntity>>

    @Query(
        """
            SELECT 
                $dbTypeTagRelatedWordRelationId as id,
                COUNT(*) as count
            FROM $dbTypeTagRelatedWordTable 
            GROUP BY $dbTypeTagRelatedWordRelationId
        """
    )
    abstract fun getRelatedWordsCount(): Map<@MapColumn("id") Long, @MapColumn("count") Int>
}