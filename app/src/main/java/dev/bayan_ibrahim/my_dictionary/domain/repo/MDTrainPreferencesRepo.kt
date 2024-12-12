package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences

/**
 * this repo for shared logic related with [MDWordsListTrainPreferences] in more than one screen
 */
interface MDTrainPreferencesRepo {
    suspend fun getWordsIdsOfTagsAndProgressRange(viewPreferences: WordsListViewPreferences): Set<Long>
}