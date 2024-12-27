package dev.bayan_ibrahim.my_dictionary.ui.navigate.app


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import javax.inject.Inject

@HiltViewModel
class MDAppViewModel @Inject constructor(
//    private val repo: MDAppRepo
) : ViewModel() {
    private val _uiState: MDAppMutableUiState = MDAppMutableUiState(isLoading = false)
    val uiState: MDAppUiState = _uiState
    fun init() {

    }

    fun getUiActions(
        navActions: MDAppNavigationUiActions,
    ): MDAppUiActions = MDAppUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDAppNavigationUiActions,
    ): MDAppBusinessUiActions = object : MDAppBusinessUiActions {
    }
}
