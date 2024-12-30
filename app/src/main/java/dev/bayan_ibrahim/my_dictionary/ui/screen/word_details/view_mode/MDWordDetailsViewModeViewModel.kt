package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsViewModeRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordDetailsViewModeViewModel @Inject constructor(
    private val repo: MDWordDetailsViewModeRepo,
) : ViewModel() {
    private val _uiState: MDWordDetailsViewModeMutableUiState = MDWordDetailsViewModeMutableUiState()
    val uiState: MDWordDetailsViewModeUiState = _uiState
    fun initWithNavArgs(args: MDDestination.WordDetailsViewMode) {
        viewModelScope.launch {
            _uiState.onExecute {
                _uiState.word = repo.getWord(args.wordId) ?: return@onExecute false
                true
            }
        }
    }

    fun getUiActions(
        navActions: MDWordDetailsViewModeNavigationUiActions,
    ): MDWordDetailsViewModeUiActions = MDWordDetailsViewModeUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDWordDetailsViewModeNavigationUiActions,
    ): MDWordDetailsViewModeBusinessUiActions = object : MDWordDetailsViewModeBusinessUiActions {
    }
}
