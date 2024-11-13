package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.runtime.Immutable

interface MDWordSpaceBusinessUiActions {
    // work space
}

interface MDWordSpaceNavigationUiActions {

}

@Immutable
class MDWordSpaceUiActions(
    navigationActions: MDWordSpaceNavigationUiActions,
    businessActions: MDWordSpaceBusinessUiActions,
) : MDWordSpaceBusinessUiActions by businessActions, MDWordSpaceNavigationUiActions by navigationActions
