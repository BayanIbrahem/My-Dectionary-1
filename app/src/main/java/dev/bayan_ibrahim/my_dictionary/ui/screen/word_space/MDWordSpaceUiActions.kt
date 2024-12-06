package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode

interface MDWordSpaceBusinessUiActions {
    // work space
    fun onAddNewWordSpace(code: LanguageCode)
}

interface MDWordSpaceNavigationUiActions {

}

@Immutable
class MDWordSpaceUiActions(
    navigationActions: MDWordSpaceNavigationUiActions,
    businessActions: MDWordSpaceBusinessUiActions,
) : MDWordSpaceBusinessUiActions by businessActions, MDWordSpaceNavigationUiActions by navigationActions
