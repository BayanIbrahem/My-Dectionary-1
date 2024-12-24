package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordWithRelatedWordsDao {
    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWordsWithRelatedWordsRelations(): Flow<List<WordWithRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId = :id
        """
    )
    suspend fun getWordWithRelatedWordsRelation(id: Long): WordWithRelatedWordsRelation?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithRelatedWordsRelations(vararg ids: Long): Flow<List<WordWithRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithRelatedWordsRelations(ids: Collection<Long>): Flow<List<WordWithRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsWithRelatedWordsRelations(languageCode: String): Flow<List<WordWithRelatedWordsRelation>>
}