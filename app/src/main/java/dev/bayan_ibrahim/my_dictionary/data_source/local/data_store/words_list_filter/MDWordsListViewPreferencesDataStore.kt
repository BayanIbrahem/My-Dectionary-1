package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.copy
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferencesBuilder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MDWordsListViewPreferencesDataStore {
    fun getWordsListViewPreferencesStream(): Flow<MDWordsListViewPreferences>
    suspend fun getWordsListViewPreferences() = getWordsListViewPreferencesStream().first()
    suspend fun writeWordsListViewPreferences(getWordsList: (MDWordsListViewPreferences) -> MDWordsListViewPreferences)
}

class MDWordsListDataStoreViewPreferencesImpl(
    context: Context,
) : MDWordsListViewPreferencesDataStore {
    private val proto = context.wordsListViewPreferencesDataStore
    override fun getWordsListViewPreferencesStream(): Flow<MDWordsListViewPreferences> = proto.data.map {
        WordsListViewPreferencesBuilder(
            searchQuery = it.searchQuery,
            searchTarget = MDWordsListSearchTarget.entries[it.searchTargetIndex],
            selectedTags = emptySet(),
            includeSelectedTags = it.includeSelectTags,
            selectedMemorizingProbabilityGroups = it.selectedMemorizingProbabilityGroupsList.map { index ->
                MDWordsListMemorizingProbabilityGroup.entries[index]
            }.toSet(),
            sortBy = MDWordsListViewPreferencesSortBy.entries[it.sortBy],
            sortByOrder = MDWordsListSortByOrder.entries[it.sortByOrder],
        )
    }

    override suspend fun writeWordsListViewPreferences(getWordsList: (MDWordsListViewPreferences) -> MDWordsListViewPreferences) {
        proto.updateData {
            it.copy {
                val wordsList = getWordsList(getWordsListViewPreferences())
                searchQuery = wordsList.searchQuery
                searchTargetIndex = wordsList.searchTarget.ordinal
                includeSelectTags = wordsList.includeSelectedTags
                selectedMemorizingProbabilityGroups.clear()
                selectedMemorizingProbabilityGroups.addAll(wordsList.selectedMemorizingProbabilityGroups.map { enum -> enum.ordinal })
                sortBy = wordsList.sortBy.ordinal
                sortByOrder = wordsList.sortByOrder.ordinal
            }
        }
    }
}