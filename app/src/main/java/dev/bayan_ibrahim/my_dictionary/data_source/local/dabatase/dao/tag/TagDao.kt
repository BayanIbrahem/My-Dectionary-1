package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagColor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagPath
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTags(tag: Collection<TagEntity>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTags(tags: Collection<TagEntity>)

    @Query(
        """
            DELETE FROM $dbTagTable WHERE $dbTagId IN (:ids)
        """
    )
    suspend fun deleteTagsOfIds(ids: Collection<Long>)

    @Query(
        """
            DELETE FROM $dbTagTable WHERE $dbTagPath IN (:values)
        """
    )
    suspend fun deleteTagsOfValues(values: Collection<String>)

    @Query(
        """
            SELECT * FROM $dbTagTable WHERE $dbTagId = :id
        """
    )
    suspend fun getTag(id: Long): TagEntity?
    @Query(
        """
            SELECT * FROM $dbTagTable
        """
    )
    fun getAllTags(): Flow<List<TagEntity>>

    /**
     * @param pattern pass leading or training % to mach different sets of tags
     *
     */
    @Query(
        """
            SELECT * FROM $dbTagTable WHERE $dbTagPath Like :pattern
        """
    )
    fun getTagsLike(
        pattern: String,
    ): Flow<List<TagEntity>>

    @Query(
        """
            SELECT * FROM $dbTagTable WHERE $dbTagId in (:ids)
        """
    )
    fun getTagsOf(ids: Collection<Long>): Flow<List<TagEntity>>

    @Query(
        """
            SELECT * FROM $dbTagTable WHERE $dbTagColor IS NOT NULL
        """
    )
    fun getMarkerTags(): Flow<List<TagEntity>>

    @Query(
        """
            SELECT * FROM $dbTagTable WHERE $dbTagColor IS NULL
        """
    )
    fun getNotMarkerTags(): Flow<List<TagEntity>>

    @Query(
        """
            SELECT * 
            FROM $dbTagTable 
            WHERE ($dbTagColor IS NULL AND :includeNotMarker) 
            OR ($dbTagColor IS NOT NULL AND :includeMarker)
        """
    )
    fun getAllTagsOfMarkerState(
        includeMarker: Boolean,
        includeNotMarker: Boolean,
    ): Flow<List<TagEntity>>

    @Query(
        """
            SELECT * 
            FROM $dbTagTable 
            WHERE ($dbTagColor IS NULL AND :includeNotMarker) 
            OR ($dbTagColor IS NOT NULL AND :includeMarker)
        """
    )
    fun getTagsOfMarkerStateAndLanguage(
        includeMarker: Boolean,
        includeNotMarker: Boolean,
    ): Flow<List<TagEntity>>
}