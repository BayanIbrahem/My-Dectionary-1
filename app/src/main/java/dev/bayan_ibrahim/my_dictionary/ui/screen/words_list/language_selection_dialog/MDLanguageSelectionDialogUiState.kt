package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf


interface MDLanguageSelectionDialogUiState : MDUiState {
    val query: String
    val languagesWithWords: PersistentList<LanguageWordSpace>
    val languagesWithoutWords: PersistentList<LanguageWordSpace>
}

class MDLanguageSelectionDialogMutableUiState : MDLanguageSelectionDialogUiState, MDMutableUiState() {
    override var query: String by mutableStateOf("")
    override var languagesWithWords: PersistentList<LanguageWordSpace> by mutableStateOf(persistentListOf())
    override var languagesWithoutWords: PersistentList<LanguageWordSpace> by mutableStateOf(persistentListOf())
}
