package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithContextTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable

private const val getWordsWithRelatedQuery = """
            SELECT * 
            FROM $dbWordTable 
            WHERE  
                $dbWordId IN (:targetWords) AND
                $dbWordLanguageCode = :languageCode AND
                (
                    (
                        :includeMeaning AND $dbWordNormalizedMeaning LIKE :queryPattern
                    ) OR 
                    (
                        :includeTranslation AND $dbWordNormalizedTranslation LIKE :queryPattern
                    ) 
                )
        """

@Dao
interface WordsPaginatedDao {
    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY :sortBy DESC")
    fun getPaginatedWordsDescOf(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
        sortBy: String,
    ): PagingSource<Int, WordWithContextTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY :sortBy ASC")
    fun getPaginatedWordsAscOf(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
        sortBy: String,
    ): PagingSource<Int, WordWithContextTagsAndRelatedWordsRelation>
}