package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordClassWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassLanguage
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordWordClassDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagType(tag: WordWordClassEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagTypes(tags: List<WordWordClassEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagTypes(vararg tags: WordWordClassEntity): List<Long>

    @Update
    suspend fun updateTagType(vararg tag: WordWordClassEntity)

    @Update
    suspend fun updateTagTypes(tags: List<WordWordClassEntity>)

    @Delete
    suspend fun deleteTagType(tag: WordWordClassEntity)

    @Query(
        """
            DELETE FROM $dbWordClassTable WHERE $dbWordClassId = :id
        """
    )
    suspend fun deleteTagType(id: Long)

    @Query(
        """
            DELETE FROM $dbWordClassTable WHERE $dbWordClassId NOT IN (:ids) AND $dbWordClassLanguage = :language
        """
    )
    suspend fun deleteWordsClassesExclude(language: String, ids: Collection<Long>)

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordClassTable
        """
    )
    fun getAllTagTypes(): Flow<List<WordClassWithRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordClassTable WHERE $dbWordClassId = :id
        """
    )
    suspend fun getTagType(id: Long): WordClassWithRelation?

    /**
     * return only tags of languages that have ones
     */
    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordClassTable WHERE $dbWordClassLanguage = :language
        """
    )
    fun getTagTypesOfLanguage(language: String): Flow<List<WordClassWithRelation>>
}