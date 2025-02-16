package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordClassRelatedWordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelatedWord(word: WordClassRelatedWordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelatedWords(words: Collection<WordClassRelatedWordEntity>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateRelatedWord(word: WordClassRelatedWordEntity)

    @Delete
    suspend fun deleteRelatedWord(word: WordClassRelatedWordEntity)

    @Delete
    suspend fun deleteRelatedWords1(words: List<WordClassRelatedWordEntity>)

    @Query(
        """
            DELETE FROM $dbWordClassRelatedWordTable WHERE $dbWordClassRelatedWordId = :id
        """
    )
    suspend fun deleteRelatedWord(id: Long)

    @Query(
        """
            DELETE FROM $dbWordClassRelatedWordTable WHERE $dbWordClassRelatedWordBaseWordId = :baseWordId
        """
    )
    suspend fun deleteRelatedWordsOfWord(baseWordId: Long)

    @Query(
        """
            DELETE FROM $dbWordClassRelatedWordTable WHERE $dbWordClassRelatedWordId IN (:ids)
        """
    )
    suspend fun deleteRelatedWords2(ids: List<Long>)

    @Query(
        """
            SELECT * FROM $dbWordClassRelatedWordTable 
        """
    )
    fun getAllRelatedWords(): Flow<List<WordClassRelatedWordEntity>>

    @Query(
        """
            SELECT * FROM $dbWordClassRelatedWordTable WHERE $dbWordClassRelatedWordBaseWordId IN (:baseWordIds)
        """
    )
    fun getAllRelatedWordsOfWords(baseWordIds: Collection<Long>): Flow<List<WordClassRelatedWordEntity>>

    @Query(
        """
            SELECT 
                $dbWordClassRelatedWordRelationId as id,
                COUNT(*) as count
            FROM $dbWordClassRelatedWordTable 
            GROUP BY $dbWordClassRelatedWordRelationId
        """
    )
    fun getRelatedWordsCount(): Flow<Map<@MapColumn("id") Long, @MapColumn("count") Int>>

//    @Query(
//        """
//            SELECT $dbWordClassRelatedWordRelationId, COUNT($dbWordClassRelatedWordBaseWordId) as count FROM $dbWordClassRelatedWordTable
//            GROUP BY $dbWordClassRelatedWordRelationId
//        """
//    )
//    fun getWordsCountOfRelation(): Flow<Map<@MapColumn(dbWordClassRelatedWordRelationId) Long, @MapColumn("count") Int>>
}