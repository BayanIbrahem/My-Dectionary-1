package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet

interface MDWordsListUiState : MDUiState {
    val selectedWordSpace: LanguageWordSpace
    val activeLanguagesWordSpaces: PersistentList<LanguageWordSpace>
    val inactiveLanguagesWordSpaces: PersistentList<LanguageWordSpace>
    val languagesWordSpaceSearchQuery: String
    val isLanguagesWordSpacesDialogShown: Boolean
    val isLanguageWordSpaceDeleteDialogShown: Boolean
    val isLanguageWordSpaceDeleteProcessRunning: Boolean
    val words: List<Word>
    val languageTags: PersistentSet<String>
    val isSelectModeOn: Boolean
    val selectedWords: PersistentSet<Long>
    val isSelectedWordsDeleteDialogShown: Boolean
    val isSelectedWordsDeleteProcessRunning: Boolean

    // view preferences
    val isViewPreferencesDialogShown: Boolean
    val preferencesState: WordsListViewPreferencesState
    val tagSearchQuery: String
    val tagsSuggestions: List<String>
}

class MDWordsListMutableUiState(
    defaultPreferences: WordsListViewPreferences = defaultWordsListViewPreferences,
) : MDWordsListUiState, MDMutableUiState() {
    override var selectedWordSpace: LanguageWordSpace by mutableStateOf(LanguageWordSpace())
    override var activeLanguagesWordSpaces: PersistentList<LanguageWordSpace> by mutableStateOf(persistentListOf())
    override var inactiveLanguagesWordSpaces: PersistentList<LanguageWordSpace> by mutableStateOf(persistentListOf())
    override var languagesWordSpaceSearchQuery: String by mutableStateOf(INVALID_TEXT)
    override var isLanguagesWordSpacesDialogShown: Boolean by mutableStateOf(false)
    override var isLanguageWordSpaceDeleteDialogShown: Boolean by mutableStateOf(false)
    override var isLanguageWordSpaceDeleteProcessRunning: Boolean by mutableStateOf(false)
    override val words: SnapshotStateList<Word> = mutableStateListOf()
    override var languageTags: PersistentSet<String> by mutableStateOf(persistentSetOf())
    override var isSelectModeOn: Boolean by mutableStateOf(false)
    override var selectedWords: PersistentSet<Long> by mutableStateOf(persistentSetOf())
    override var isSelectedWordsDeleteDialogShown: Boolean by mutableStateOf(false)
    override var isSelectedWordsDeleteProcessRunning: Boolean by mutableStateOf(false)
    override var isViewPreferencesDialogShown: Boolean by mutableStateOf(false)

    override val preferencesState = WordsListViewPreferencesMutableState(defaultPreferences)
    override var tagSearchQuery: String by mutableStateOf(INVALID_TEXT)
    override val tagsSuggestions: SnapshotStateList<String> = mutableStateListOf()
}


interface WordsListViewPreferencesState : WordsListViewPreferences

class WordsListViewPreferencesMutableState(
    searchQuery: String = defaultWordsListViewPreferences.searchQuery,
    searchTarget: WordsListSearchTarget = defaultWordsListViewPreferences.searchTarget,
    selectedTags: Set<String> = defaultWordsListViewPreferences.selectedTags,
    includeSelectedTags: Boolean = defaultWordsListViewPreferences.includeSelectedTags,
    selectedLearningProgressGroups: Set<WordsListLearningProgressGroup> = defaultWordsListViewPreferences.selectedLearningProgressGroups,
    sortBy: WordsListSortBy = defaultWordsListViewPreferences.sortBy,
    sortByOrder: WordsListSortByOrder = defaultWordsListViewPreferences.sortByOrder,
) : WordsListViewPreferencesState {
    constructor(data: WordsListViewPreferences) : this(
        searchQuery = data.searchQuery,
        searchTarget = data.searchTarget,
        selectedTags = data.selectedTags,
        includeSelectedTags = data.includeSelectedTags,
        selectedLearningProgressGroups = data.selectedLearningProgressGroups,
        sortBy = data.sortBy,
        sortByOrder = data.sortByOrder
    )

    override var searchQuery: String by mutableStateOf(searchQuery)
    override var searchTarget: WordsListSearchTarget by mutableStateOf(searchTarget)
    override var selectedTags: PersistentSet<String> by mutableStateOf(selectedTags.toPersistentSet())
    override var includeSelectedTags: Boolean by mutableStateOf(includeSelectedTags)
    override var selectedLearningProgressGroups: PersistentSet<WordsListLearningProgressGroup> by mutableStateOf(
        selectedLearningProgressGroups.toPersistentSet()
    )
    override var sortBy: WordsListSortBy by mutableStateOf(sortBy)
    override var sortByOrder: WordsListSortByOrder by mutableStateOf(sortByOrder)

    fun onApplyPreferences(preferences: WordsListViewPreferences) {
        searchQuery = preferences.searchQuery
        searchTarget = preferences.searchTarget
        selectedTags = preferences.selectedTags.toPersistentSet()
        includeSelectedTags = preferences.includeSelectedTags
        selectedLearningProgressGroups = preferences.selectedLearningProgressGroups.toPersistentSet()
        sortBy = preferences.sortBy
        sortByOrder = preferences.sortByOrder
    }
}