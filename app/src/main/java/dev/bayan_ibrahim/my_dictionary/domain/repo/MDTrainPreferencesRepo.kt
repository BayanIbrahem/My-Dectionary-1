package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListTrainPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences

/**
 * this repo for shared logic related with [MDWordsListTrainPreferencesDataStore] in more than one screen
 */
interface MDTrainPreferencesRepo {
    suspend fun getWordsIdsOfTagsAndProgressRange(viewPreferences: MDWordsListViewPreferences): Set<Long>
}