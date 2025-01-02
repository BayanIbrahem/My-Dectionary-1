package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.StateFlow

interface MDWordsListUiState : MDUiState {
    val selectedWordSpace: StateFlow<LanguageWordSpace>
    val isLanguagesWordSpacesDialogShown: Boolean
    val isLanguageWordSpaceDeleteDialogShown: Boolean
    val isLanguageWordSpaceDeleteProcessRunning: Boolean
    val languageTags: PersistentSet<String>
    val isSelectModeOn: Boolean
    val selectedWords: PersistentSet<Long>
    val isSelectedWordsDeleteDialogShown: Boolean
    val isSelectedWordsDeleteProcessRunning: Boolean

    // view preferences
    val showViewPreferencesDialog: Boolean
    val viewPreferencesQuery: Pair<String?, String?>

    /**
     * if the view preferences affects the words list or not, this is used to display placeholder message
     * when no words are displayed in the list screen
     */
    val isViewPreferencesEffectiveFilter: Boolean

    // train preferences
    val showTrainPreferencesDialog: Boolean
}

class MDWordsListMutableUiState(
    override val selectedWordSpace: StateFlow<LanguageWordSpace>,
) : MDWordsListUiState, MDMutableUiState() {
    override var isLanguagesWordSpacesDialogShown: Boolean by mutableStateOf(false)
    override var isLanguageWordSpaceDeleteDialogShown: Boolean by mutableStateOf(false)
    override var isLanguageWordSpaceDeleteProcessRunning: Boolean by mutableStateOf(false)
    override var languageTags: PersistentSet<String> by mutableStateOf(persistentSetOf())
    override var isSelectModeOn: Boolean by mutableStateOf(false)
    override var selectedWords: PersistentSet<Long> by mutableStateOf(persistentSetOf())
    override var isSelectedWordsDeleteDialogShown: Boolean by mutableStateOf(false)
    override var isSelectedWordsDeleteProcessRunning: Boolean by mutableStateOf(false)

    // view preferences:
    override var showViewPreferencesDialog: Boolean by mutableStateOf(false)
    override var isViewPreferencesEffectiveFilter: Boolean by mutableStateOf(false)
    override var viewPreferencesQuery: Pair<String?, String?> by mutableStateOf(null to null)

    // train preferences
    override var showTrainPreferencesDialog: Boolean by mutableStateOf(false)
}
