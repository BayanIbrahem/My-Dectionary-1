package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpaceState

interface MDWordSpaceUiState : MDUiState {
    val wordSpaces: List<LanguageWordSpaceState>
}

class MDWordSpaceMutableUiState : MDWordSpaceUiState, MDMutableUiState() {
    override val wordSpaces: SnapshotStateList<LanguageWordSpaceState> = mutableStateListOf()
}