package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.invalid


interface MDWordDetailsViewModeUiState : MDUiState {
    val word: Word
}

class MDWordDetailsViewModeMutableUiState : MDWordDetailsViewModeUiState, MDMutableUiState() {
    override var word: Word by mutableStateOf(Word.invalid())
}
