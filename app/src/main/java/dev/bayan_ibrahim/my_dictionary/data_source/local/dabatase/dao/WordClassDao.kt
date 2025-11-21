package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordClassWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassLanguage
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordClassDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordClass(tag: WordClassEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagTypes(tags: List<WordClassEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagTypes(vararg tags: WordClassEntity): List<Long>

    @Update
    suspend fun updateTagType(vararg tag: WordClassEntity)

    @Update
    suspend fun updateTagTypes(tags: List<WordClassEntity>)

    @Delete
    suspend fun deleteTagType(tag: WordClassEntity)

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
    fun getAllWordClasses(): Flow<List<WordClassWithRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordClassTable WHERE $dbWordClassId = :id
        """
    )
    suspend fun getWordClass(id: Long): WordClassWithRelation?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordClassTable WHERE $dbWordClassName = :name
        """
    )
    suspend fun getWordClass(name: String): WordClassWithRelation?

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