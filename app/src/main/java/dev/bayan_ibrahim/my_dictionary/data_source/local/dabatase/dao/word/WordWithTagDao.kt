package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithTagsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WordWithTagDao {
    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable
        """
    )
    fun getAllWordsWithTagsRelations(): Flow<List<WordWithTagsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId = :id
        """
    )
    suspend fun getWordWithTagsRelation(id: Long): WordWithTagsRelation?

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithTagsRelations(vararg ids: Long): Flow<List<WordWithTagsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordId in (:ids)
        """
    )
    fun getWordsWithTagsRelations(ids: Collection<Long>): Flow<List<WordWithTagsRelation>>

    @Transaction
    @Query(
        """
            SELECT * FROM $dbWordTable WHERE $dbWordLanguageCode = :languageCode
        """
    )
    fun getWordsWithTagsRelations(languageCode: String): Flow<List<WordWithTagsRelation>>
}