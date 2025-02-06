package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTagId
import kotlinx.coroutines.flow.Flow

@Dao
interface WordWordClassRelationWordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(relation: WordWordClassRelationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelations(relations: List<WordWordClassRelationEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelations(vararg relations: WordWordClassRelationEntity): List<Long>

    @Update
    suspend fun updateRelation(relation: WordWordClassRelationEntity)

    @Update
    suspend fun updateRelations(relations: List<WordWordClassRelationEntity>)

    @Update
    suspend fun updateRelations(vararg relations: WordWordClassRelationEntity)

    @Delete
    suspend fun deleteRelation(relation: WordWordClassRelationEntity)

    @Delete
    suspend fun deleteRelations1(relations: List<WordWordClassRelationEntity>)

    @Delete
    suspend fun deleteRelations2(vararg relations: WordWordClassRelationEntity)

    @Query(
        """
            DELETE FROM $dbWordClassRelationTable WHERE $dbWordClassRelationId = :id
        """
    )
    suspend fun deleteRelation(id: Long)

    @Query(
        """
            DELETE FROM $dbWordClassRelationTable WHERE $dbWordClassRelationId IN (:ids)
        """
    )
    suspend fun deleteRelations(ids: List<Long>)
    @Query(
        """
            DELETE FROM $dbWordClassRelationTable WHERE $dbWordClassRelationTagId = :tagId AND $dbWordClassRelationId NOT IN (:ids)
        """
    )
    suspend fun deleteRelationsNotIn(ids: Collection<Long>, tagId: Long)

    @Query(
        """
            DELETE FROM $dbWordClassRelationTable WHERE $dbWordClassRelationId IN (:ids)
        """
    )
    suspend fun deleteRelations(vararg ids: Long)

    @Query(
        """
            SELECT * FROM $dbWordClassRelationTable 
        """
    )
    fun getAllRelations(): Flow<List<WordWordClassRelationEntity>>

    @Query(
        """
            SELECT * FROM $dbWordClassRelationTable WHERE $dbWordClassRelationId = :id
        """
    )
    suspend fun getAllRelation(id: Long): WordWordClassRelationEntity?
}