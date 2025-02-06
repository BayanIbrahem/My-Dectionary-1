package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTagId
import kotlinx.coroutines.flow.Flow

@Dao
interface WordClassRelationWordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(relation: WordClassRelationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelations(relations: List<WordClassRelationEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelations(vararg relations: WordClassRelationEntity): List<Long>

    @Update
    suspend fun updateRelation(relation: WordClassRelationEntity)

    @Update
    suspend fun updateRelations(relations: List<WordClassRelationEntity>)

    @Update
    suspend fun updateRelations(vararg relations: WordClassRelationEntity)

    @Delete
    suspend fun deleteRelation(relation: WordClassRelationEntity)

    @Delete
    suspend fun deleteRelations1(relations: List<WordClassRelationEntity>)

    @Delete
    suspend fun deleteRelations2(vararg relations: WordClassRelationEntity)

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
    fun getAllRelations(): Flow<List<WordClassRelationEntity>>

    @Query(
        """
            SELECT * FROM $dbWordClassRelationTable WHERE $dbWordClassRelationId = :id
        """
    )
    suspend fun getAllRelation(id: Long): WordClassRelationEntity?
}