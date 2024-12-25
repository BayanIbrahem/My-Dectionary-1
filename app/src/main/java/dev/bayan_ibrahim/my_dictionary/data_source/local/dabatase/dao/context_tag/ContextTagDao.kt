package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.ContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagPath
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ContextTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContextTag(tag: ContextTagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContextTags(tag: Collection<ContextTagEntity>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateContextTag(tag: ContextTagEntity)

    @Delete
    suspend fun deleteContextTags(tags: Collection<ContextTagEntity>)

    @Query(
        """
            DELETE FROM $dbContextTagTable WHERE $dbContextTagId IN (:ids)
        """
    )
    suspend fun deleteContextTagsOfIds(ids: Collection<Long>)

    @Query(
        """
            DELETE FROM $dbContextTagTable WHERE $dbContextTagPath IN (:values)
        """
    )
    suspend fun deleteContextTagsOfValues(values: Collection<String>)

    @Query(
        """
            SELECT * FROM $dbContextTagTable WHERE $dbContextTagId = :id
        """
    )
    suspend fun getContextTag(id: Long): ContextTagEntity?
    @Query(
        """
            SELECT * FROM $dbContextTagTable
        """
    )
    fun getAllContextTags(): Flow<List<ContextTagEntity>>

    /**
     * @param pattern pass leading or training % to mach different sets of tags
     *
     */
    @Query(
        """
            SELECT * FROM $dbContextTagTable WHERE $dbContextTagPath Like :pattern
        """
    )
    fun getContextTagsLike(
        pattern: String,
    ): Flow<List<ContextTagEntity>>

    @Query(
        """
            SELECT * FROM $dbContextTagTable WHERE $dbContextTagId in (:ids)
        """
    )
    fun getContextTagsOf(ids: Collection<Long>): Flow<List<ContextTagEntity>>
}