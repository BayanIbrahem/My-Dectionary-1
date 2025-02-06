package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.WordDetailsDirectionSource
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordDetailsViewModeViewModel @Inject constructor(
    private val wordRepo: WordRepo,
    private val preferencesRepo: UserPreferencesRepo,
) : ViewModel() {
    private val _uiState: MDWordDetailsViewModeMutableUiState = MDWordDetailsViewModeMutableUiState()
    val uiState: MDWordDetailsViewModeUiState = _uiState
    val wordDetailsDirectionSource = preferencesRepo.getUserPreferencesStream().map {
        it.wordDetailsDirectionSource
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WordDetailsDirectionSource.WordLanguage
    )

    fun initWithNavArgs(args: MDDestination.WordDetailsViewMode) {
        viewModelScope.launch {
            _uiState.onExecute {
                _uiState.word = wordRepo.getWord(args.wordId) ?: return@onExecute false
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
        override fun onToggleWordDetailsAlignmentSource(source: WordDetailsDirectionSource?) {
            viewModelScope.launch {
                preferencesRepo.setUserPreferences {
                    it.copy(wordDetailsDirectionSource = source ?: WordDetailsDirectionSource.WordLanguage)
                }
            }
        }
    }
}
