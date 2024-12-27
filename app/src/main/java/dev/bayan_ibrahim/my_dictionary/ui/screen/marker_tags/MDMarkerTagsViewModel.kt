package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import javax.inject.Inject

@HiltViewModel
class MDMarkerTagsViewModel @Inject constructor(
//    private val repo: MDMarkerTagsRepo
) : ViewModel() {
    private val _uiState: MDMarkerTagsMutableUiState = MDMarkerTagsMutableUiState()
    val uiState: MDMarkerTagsUiState = _uiState
    fun initWithNavArgs(args: MDDestination) {

    }

    fun getUiActions(
        navActions: MDMarkerTagsNavigationUiActions,
    ): MDMarkerTagsUiActions = MDMarkerTagsUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDMarkerTagsNavigationUiActions,
    ): MDMarkerTagsBusinessUiActions = object : MDMarkerTagsBusinessUiActions {
    }
}
