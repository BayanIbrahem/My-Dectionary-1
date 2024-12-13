package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog

import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace


interface MDLanguageSelectionDialogBusinessUiActions {
    fun onSelectWordSpace(languageWordSpace: LanguageWordSpace)
    fun onQueryChange(query: String)
}

interface MDLanguageSelectionDialogNavigationUiActions {
    fun onDismissRequest()
}

@androidx.compose.runtime.Immutable
class MDLanguageSelectionDialogUiActions(
    navigationActions: MDLanguageSelectionDialogNavigationUiActions,
    businessActions: MDLanguageSelectionDialogBusinessUiActions,
) : MDLanguageSelectionDialogBusinessUiActions by businessActions, MDLanguageSelectionDialogNavigationUiActions by navigationActions