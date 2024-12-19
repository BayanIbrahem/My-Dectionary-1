package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDProfileViewModel @Inject constructor(
//    private val repo: MDProfileRepo
    private val datastore: MDPreferencesDataStore,
) : ViewModel() {
    private val _uiState: MDProfileMutableUiState = MDProfileMutableUiState()
    val uiState: MDProfileUiState = _uiState
    val userPreferences = datastore.getUserPreferencesStream().stateIn(
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
                datastore.writeUserPreferences {
                    it.copy(liveMemorizingProbability = liveTemplate)
                }
            }
        }
    }
}