package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.MDWordsListMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.MDWordsListUiState
import javax.inject.Inject

@HiltViewModel
class MDWordSpaceViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MDWordSpaceMutableUiState = MDWordSpaceMutableUiState()
    val uiState: MDWordSpaceUiState = _uiState

    fun initWithNavArgs(args: MDDestination.TopLevel.WordSpace) {

    }
    fun getUiActions(
        navActions: MDWordSpaceNavigationUiActions,
    ) = MDWordSpaceUiActions(
        navigationActions = navActions,
        businessActions = getBusinessActions(navActions)
    )

    private fun getBusinessActions(
        navActions: MDWordSpaceNavigationUiActions,
    ): MDWordSpaceBusinessUiActions = object : MDWordSpaceBusinessUiActions {

    }
}