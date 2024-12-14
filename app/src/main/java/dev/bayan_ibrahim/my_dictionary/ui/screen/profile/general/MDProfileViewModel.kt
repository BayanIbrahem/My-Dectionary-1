package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import javax.inject.Inject

@HiltViewModel
class MDProfileViewModel @Inject constructor(
//    private val repo: MDProfileRepo
) : ViewModel() {
    private val _uiState: MDProfileMutableUiState = MDProfileMutableUiState()
    val uiState: MDProfileUiState = _uiState
    fun initWithNavArgs(args: MDDestination.TopLevel.Profile) {
        _uiState.onExecute { true }
    }

    fun getUiActions(
        navActions: MDProfileNavigationUiActions,
    ): MDProfileUiActions = MDProfileUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDProfileNavigationUiActions,
    ): MDProfileBusinessUiActions = object : MDProfileBusinessUiActions {
    }
}