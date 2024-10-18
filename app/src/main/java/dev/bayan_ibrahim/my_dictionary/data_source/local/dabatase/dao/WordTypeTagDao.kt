package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.TypeTagWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagLanguage
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordTypeTagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagType(tag: WordTypeTagEntity)

    @Update
    suspend fun updateTagType(vararg tag: WordTypeTagEntity)

    @Update
    suspend fun updateTagType(tags: List<WordTypeTagEntity>)

    @Delete
    suspend fun deleteTagType(tag: WordTypeTagEntity)

    @Query(
        """
            DELETE FROM $dbTypeTagTable WHERE $dbTypeTagId = :id
        """
    )
    suspend fun deleteTagType(id: Long)

    @Transaction
    @Query(
        """
            SELECT * FROM $dbTypeTagTable
        """
    )
    fun getAllTagTypes(): Flow<List<TypeTagWithRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbTypeTagTable WHERE $dbTypeTagId = :id
        """
    )
    suspend fun getTagType(id: Long): TypeTagWithRelation?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbTypeTagTable WHERE $dbTypeTagLanguage = :language
        """
    )
    fun getTagTypesOfLanguage(language: String): Flow<List<TypeTagWithRelation>>
}