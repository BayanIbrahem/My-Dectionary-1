package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordDetailsDirectionSource
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDProfileViewModel @Inject constructor(
    private val userRepo: UserPreferencesRepo,
) : ViewModel() {
    private val _uiState: MDProfileMutableUiState = MDProfileMutableUiState()
    val uiState: MDProfileUiState = _uiState
    val userPreferences = userRepo.getUserPreferencesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MDUserPreferences()
    )

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
        override fun onToggleLiveTemplate(liveTemplate: Boolean) {
            viewModelScope.launch {
                userRepo.setUserPreferences {
                    it.copy(liveMemorizingProbability = liveTemplate)
                }
            }
        }

        override fun onToggleWordDetailsAlignmentSource(source: WordDetailsDirectionSource) {
            viewModelScope.launch {
                userRepo.setUserPreferences {
                    it.copy(wordDetailsDirectionSource = source)
                }
            }
        }
    }
}