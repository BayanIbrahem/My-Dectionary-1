package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace


sealed class MDLanguageSelectionDialogUiState : MDUiState {
    abstract val selectedWordSpace: LanguageWordSpace?
    abstract val query: String
    abstract val languagesWithWords: List<LanguageWordSpace>
    abstract val languagesWithoutWords: List<LanguageWordSpace>

    data object Loading : MDLanguageSelectionDialogUiState() {
        override val selectedWordSpace: LanguageWordSpace? = null
        override val query: String = ""
        override val languagesWithWords: List<LanguageWordSpace> = emptyList()
        override val languagesWithoutWords: List<LanguageWordSpace> = emptyList()
        override val isLoading: Boolean = true
        override val error: String? = null
        override val validData: Boolean = true
    }

    data class Data(
        override val selectedWordSpace: LanguageWordSpace?,
        override val query: String,
        override val languagesWithWords: List<LanguageWordSpace>,
        override val languagesWithoutWords: List<LanguageWordSpace>,
    ) : MDLanguageSelectionDialogUiState() {
        override val isLoading: Boolean = false
        override val error: String? = null
        override val validData: Boolean = true
    }
}

