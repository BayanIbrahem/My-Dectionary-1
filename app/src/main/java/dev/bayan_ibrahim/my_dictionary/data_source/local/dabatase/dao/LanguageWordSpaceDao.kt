package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao

import androidx.room.Dao
import androidx.room.Query
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceAverageMemorizingProbability
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbLanguageWordSpaceWordsCount
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordLanguageCode
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordMemoryDecayFactor
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordTable
import kotlinx.coroutines.flow.Flow


private const val select_from = """
            SELECT 
                $dbLanguageCode as $dbLanguageWordSpaceLanguageCode, 
                COUNT(*) as $dbLanguageWordSpaceWordsCount, 
                AVG($dbWordMemoryDecayFactor) $dbLanguageWordSpaceAverageMemorizingProbability
            FROM $dbLanguageTable LEFT JOIN $dbWordTable On $dbLanguageTable.$dbLanguageCode = $dbWordTable.$dbWordLanguageCode
    """

private const val group_order_by = """
            GROUP BY $dbLanguageCode
            ORDER BY $dbLanguageWordSpaceWordsCount DESC
"""

@Dao
interface LanguageWordSpaceDao {
    @Query(
        """
            $select_from
            $group_order_by
    """
    )
    fun getLanguagesWordSpaces(): Flow<List<LanguageWordSpaceEntity>>

    @Query(
        """
            $select_from
            WHERE $dbWordLanguageCode = :languageCode
            $group_order_by
            LIMIT 1
    """
    )
    suspend fun getLanguagesWordSpace(languageCode: String): LanguageWordSpaceEntity?
}