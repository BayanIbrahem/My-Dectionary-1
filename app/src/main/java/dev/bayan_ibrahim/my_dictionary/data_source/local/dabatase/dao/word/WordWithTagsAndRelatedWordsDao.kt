package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordWithTagsAndRelatedWordsDao {
    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWordsWithTagsAndRelatedWordsRelations(): Flow<List<WordWithTagsAndRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId = :id
        """
    )
    suspend fun getWordWithTagsAndRelatedWordsRelation(id: Long): WordWithTagsAndRelatedWordsRelation?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithTagsAndRelatedWordsRelations(vararg ids: Long): Flow<List<WordWithTagsAndRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithTagsAndRelatedWordsRelations(ids: Collection<Long>): Flow<List<WordWithTagsAndRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsWithTagsAndRelatedWordsRelations(languageCode: String): Flow<List<WordWithTagsAndRelatedWordsRelation>>
}