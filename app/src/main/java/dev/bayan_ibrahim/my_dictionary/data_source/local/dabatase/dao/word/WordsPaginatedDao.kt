package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordCreatedAt
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedMeaning
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordNormalizedTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTranslation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordUpdatedAt

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
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordMeaning DESC")
    fun getPaginatedWordsDescOfMeaning(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordMeaning ASC")
    fun getPaginatedWordsAscOfMeaning(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>


    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordTranslation DESC")
    fun getPaginatedWordsDescOfTranslation(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordTranslation ASC")
    fun getPaginatedWordsAscOfTranslation(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordCreatedAt DESC")
    fun getPaginatedWordsDescOfCreatedAt(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordCreatedAt ASC")
    fun getPaginatedWordsAscOfCreatedAt(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordUpdatedAt DESC")
    fun getPaginatedWordsDescOfUpdatedAt(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>

    @Transaction
    @Query("$getWordsWithRelatedQuery ORDER BY $dbWordUpdatedAt ASC")
    fun getPaginatedWordsAscOfUpdatedAt(
        languageCode: String,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        queryPattern: String,
    ): PagingSource<Int, WordWithTagsAndRelatedWordsRelation>
}