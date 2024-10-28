package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.copy
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferencesBuilder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListSortByOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MDWordsListViewPreferences {
    fun getWordsListViewPreferencesStream(): Flow<WordsListViewPreferences>
    suspend fun getWordsListViewPreferences() = getWordsListViewPreferencesStream().first()
    suspend fun writeWordsListViewPreferences(getWordsList: (WordsListViewPreferences) -> WordsListViewPreferences)
}

class MDWordsListViewPreferencesImpl(
    context: Context,
) : MDWordsListViewPreferences {
    private val proto = context.wordsListDataStore
    override fun getWordsListViewPreferencesStream(): Flow<WordsListViewPreferences> = proto.data.map {
        WordsListViewPreferencesBuilder(
            searchQuery = it.searchQuery,
            searchTarget = WordsListSearchTarget.entries[it.searchTargetIndex],
            selectedTags = it.selectedTagsList.toSet(),
            includeSelectedTags = it.includeSelectTags,
            selectedLearningProgressGroups = it.selectedLearningProgressGroupsList.map { index ->
                WordsListLearningProgressGroup.entries[index]
            }.toSet(),
            sortBy = WordsListSortBy.entries[it.sortBy],
            sortByOrder = WordsListSortByOrder.entries[it.sortByOrder],
        )
    }

    override suspend fun writeWordsListViewPreferences(getWordsList: (WordsListViewPreferences) -> WordsListViewPreferences) {
        proto.updateData {
            it.copy {
                val wordsList = getWordsList(getWordsListViewPreferences())
                searchQuery = wordsList.searchQuery
                searchTargetIndex = wordsList.searchTarget.ordinal
                selectedTags.clear()
                selectedTags.addAll(wordsList.selectedTags)
                includeSelectTags = wordsList.includeSelectedTags
                selectedLearningProgressGroups.clear()
                selectedLearningProgressGroups.addAll(wordsList.selectedLearningProgressGroups.map { enum -> enum.ordinal })
                sortBy = wordsList.sortBy.ordinal
                sortByOrder = wordsList.sortByOrder.ordinal
            }
        }
    }
}