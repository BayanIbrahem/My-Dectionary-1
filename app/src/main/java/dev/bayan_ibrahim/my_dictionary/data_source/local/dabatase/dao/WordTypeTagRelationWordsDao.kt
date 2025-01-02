package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationTagId
import kotlinx.coroutines.flow.Flow

@Dao
interface WordTypeTagRelationWordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(relation: WordTypeTagRelationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelations(relations: List<WordTypeTagRelationEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelations(vararg relations: WordTypeTagRelationEntity): List<Long>

    @Update
    suspend fun updateRelation(relation: WordTypeTagRelationEntity)

    @Update
    suspend fun updateRelations(relations: List<WordTypeTagRelationEntity>)

    @Update
    suspend fun updateRelations(vararg relations: WordTypeTagRelationEntity)

    @Delete
    suspend fun deleteRelation(relation: WordTypeTagRelationEntity)

    @Delete
    suspend fun deleteRelations1(relations: List<WordTypeTagRelationEntity>)

    @Delete
    suspend fun deleteRelations2(vararg relations: WordTypeTagRelationEntity)

    @Query(
        """
            DELETE FROM $dbTypeTagRelationTable WHERE $dbTypeTagRelationId = :id
        """
    )
    suspend fun deleteRelation(id: Long)

    @Query(
        """
            DELETE FROM $dbTypeTagRelationTable WHERE $dbTypeTagRelationId IN (:ids)
        """
    )
    suspend fun deleteRelations(ids: List<Long>)
    @Query(
        """
            DELETE FROM $dbTypeTagRelationTable WHERE $dbTypeTagRelationTagId = :tagId AND $dbTypeTagRelationId NOT IN (:ids)
        """
    )
    suspend fun deleteRelationsNotIn(ids: Collection<Long>, tagId: Long)

    @Query(
        """
            DELETE FROM $dbTypeTagRelationTable WHERE $dbTypeTagRelationId IN (:ids)
        """
    )
    suspend fun deleteRelations(vararg ids: Long)

    @Query(
        """
            SELECT * FROM $dbTypeTagRelationTable 
        """
    )
    fun getAllRelations(): Flow<List<WordTypeTagRelationEntity>>

    @Query(
        """
            SELECT * FROM $dbTypeTagRelationTable WHERE $dbTypeTagRelationId = :id
        """
    )
    suspend fun getAllRelation(id: Long): WordTypeTagRelationEntity?
}