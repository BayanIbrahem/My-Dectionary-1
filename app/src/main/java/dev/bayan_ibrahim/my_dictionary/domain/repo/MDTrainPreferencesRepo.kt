package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListTrainPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

/**
 * this repo for shared logic related with [MDWordsListTrainPreferencesDataStore] in more than one screen
 */
interface MDTrainPreferencesRepo {
    suspend fun getWordsIdsOfTagsAndMemorizingProbability(
        language: Language,
        viewPreferences: MDWordsListViewPreferences
    ): Set<Long>
}