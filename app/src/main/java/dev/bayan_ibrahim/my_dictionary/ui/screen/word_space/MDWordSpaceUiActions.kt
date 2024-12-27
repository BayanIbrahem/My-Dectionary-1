package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDWordSpaceBusinessUiActions {
    // work space
    fun onAddNewWordSpace(code: LanguageCode)
}

interface MDWordSpaceNavigationUiActions: MDAppNavigationUiActions {
    fun navigateToStatistics(language: Language)
}

@Immutable
class MDWordSpaceUiActions(
    navigationActions: MDWordSpaceNavigationUiActions,
    businessActions: MDWordSpaceBusinessUiActions,
) : MDWordSpaceBusinessUiActions by businessActions, MDWordSpaceNavigationUiActions by navigationActions
