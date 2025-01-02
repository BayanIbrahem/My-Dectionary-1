package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageTable
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLanguage(language: LanguageEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllLanguages(languages: Collection<LanguageEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllLanguages(vararg languages: LanguageEntity)

    @Delete
    suspend fun deleteLanguage(language: LanguageEntity)

    @Delete
    suspend fun deleteAllLanguages(languages: Collection<LanguageEntity>)

    @Query(
        """
            DELETE FROM $dbLanguageTable WHERE $dbLanguageCode = :code
        """
    )
    suspend fun deleteLanguage(code: String)

    @Query(
        """
            DELETE FROM $dbLanguageTable WHERE $dbLanguageCode IN (:codes)
        """
    )
    suspend fun deleteAllLanguage(codes: Collection<String>)

    @Query(
        """
            DELETE FROM $dbLanguageTable WHERE $dbLanguageCode IN (:codes)
        """
    )
    suspend fun deleteAllLanguage(vararg codes: String)


    @Query(
        """
            SELECT * FROM $dbLanguageTable 
        """
    )
    fun getAllLanguages(): Flow<List<LanguageEntity>>

    @Query(
        """
            SELECT Count(*) > 0 FROM $dbLanguageTable  WHERE $dbLanguageCode = :code
        """
    )
    suspend fun hasLanguage(code: String): Boolean

    @Query(
        """
            SELECT Count(*) > 0 FROM $dbLanguageTable  WHERE $dbLanguageCode IN (:code)
        """
    )
    suspend fun hasLanguages(code: Collection<String>): Boolean
}