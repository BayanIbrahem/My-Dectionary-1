package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceState

interface MDWordSpaceUiState : MDUiState {
    val currentEditableWordSpaceLanguageCode: LanguageCode?
    val wordSpacesWithActions: List<Pair<LanguageWordSpaceState, LanguageWordSpaceActions>>
}

class MDWordSpaceMutableUiState : MDWordSpaceUiState, MDMutableUiState() {
    override var currentEditableWordSpaceLanguageCode: LanguageCode? by mutableStateOf(null)
    override val wordSpacesWithActions: SnapshotStateList<Pair<LanguageWordSpaceMutableState, LanguageWordSpaceActions>> = mutableStateListOf()
}