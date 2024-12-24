package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithContextTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordWithContextTagsAndRelatedWordsDao {
    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWordsWithContextTagsAndRelatedWordsRelations(): Flow<List<WordWithContextTagsAndRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId = :id
        """
    )
    suspend fun getWordWithContextTagsAndRelatedWordsRelation(id: Long): WordWithContextTagsAndRelatedWordsRelation?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithContextTagsAndRelatedWordsRelations(vararg ids: Long): Flow<List<WordWithContextTagsAndRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithContextTagsAndRelatedWordsRelations(ids: Collection<Long>): Flow<List<WordWithContextTagsAndRelatedWordsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsWithContextTagsAndRelatedWordsRelations(languageCode: String): Flow<List<WordWithContextTagsAndRelatedWordsRelation>>
}