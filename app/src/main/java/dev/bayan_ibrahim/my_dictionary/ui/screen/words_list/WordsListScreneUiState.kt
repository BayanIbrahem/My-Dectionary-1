package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences.WordsListTrainPreferencesMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences.WordsListTrainPreferencesState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences.WordsListViewPreferencesMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences.WordsListViewPreferencesState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

interface MDWordsListUiState : MDUiState {
    val selectedWordSpace: LanguageWordSpace
    val activeLanguagesWordSpaces: PersistentList<LanguageWordSpace>
    val inactiveLanguagesWordSpaces: PersistentList<LanguageWordSpace>
    val languagesWordSpaceSearchQuery: String
    val isLanguagesWordSpacesDialogShown: Boolean
    val isLanguageWordSpaceDeleteDialogShown: Boolean
    val isLanguageWordSpaceDeleteProcessRunning: Boolean
    val languageTags: PersistentSet<String>
    val isSelectModeOn: Boolean
    val selectedWords: PersistentSet<Long>
    val isSelectedWordsDeleteDialogShown: Boolean
    val isSelectedWordsDeleteProcessRunning: Boolean

    // view preferences
    val viewPreferencesState: WordsListViewPreferencesState
    val tagSearchQuery: String
    val tagsSuggestions: List<String>

    // train preferences
    val trainPreferencesState: WordsListTrainPreferencesState
}

class MDWordsListMutableUiState(
    defaultViewPreferences: WordsListViewPreferences = defaultWordsListViewPreferences,
    defaultTrainPreferences: WordsListTrainPreferences = defaultWordsListTrainPreferences,
) : MDWordsListUiState, MDMutableUiState() {
    override var selectedWordSpace: LanguageWordSpace by mutableStateOf(LanguageWordSpace())
    override var activeLanguagesWordSpaces: PersistentList<LanguageWordSpace> by mutableStateOf(persistentListOf())
    override var inactiveLanguagesWordSpaces: PersistentList<LanguageWordSpace> by mutableStateOf(persistentListOf())
    override var languagesWordSpaceSearchQuery: String by mutableStateOf(INVALID_TEXT)
    override var isLanguagesWordSpacesDialogShown: Boolean by mutableStateOf(false)
    override var isLanguageWordSpaceDeleteDialogShown: Boolean by mutableStateOf(false)
    override var isLanguageWordSpaceDeleteProcessRunning: Boolean by mutableStateOf(false)
    override var languageTags: PersistentSet<String> by mutableStateOf(persistentSetOf())
    override var isSelectModeOn: Boolean by mutableStateOf(false)
    override var selectedWords: PersistentSet<Long> by mutableStateOf(persistentSetOf())
    override var isSelectedWordsDeleteDialogShown: Boolean by mutableStateOf(false)
    override var isSelectedWordsDeleteProcessRunning: Boolean by mutableStateOf(false)

    // view preferences:
    override val viewPreferencesState: WordsListViewPreferencesMutableState = WordsListViewPreferencesMutableState(defaultViewPreferences)
    override var tagSearchQuery: String by mutableStateOf(INVALID_TEXT)
    override val tagsSuggestions: SnapshotStateList<String> = mutableStateListOf()

    // train preferences preferences:
    override val trainPreferencesState: WordsListTrainPreferencesMutableState = WordsListTrainPreferencesMutableState(defaultTrainPreferences)
}
