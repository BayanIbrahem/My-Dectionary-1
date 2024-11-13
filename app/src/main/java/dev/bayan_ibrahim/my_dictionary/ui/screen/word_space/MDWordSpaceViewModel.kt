package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.toMDEditableField
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordSpaceViewModel @Inject constructor(
    private val repo: MDWordSpaceRepo,
) : ViewModel() {
    private val _uiState: MDWordSpaceMutableUiState = MDWordSpaceMutableUiState()
    val uiState: MDWordSpaceUiState = _uiState

    fun initWithNavArgs(args: MDDestination.TopLevel.WordSpace) {
        viewModelScope.launch {
            _uiState.isLoading = true

            repo.getLanguagesWordSpacesWithTags().forEach { (wordSpace, tags) ->
                _uiState.wordSpacesWithActions.add(
                    LanguageWordSpaceMutableState(
                        wordSpace = wordSpace,
                        initialTags = tags.map { it.toMDEditableField(false) }
                    ).let {
                        it to it.getActions(
                            scope = viewModelScope,
                            onSubmitRequest = ::onSubmitWordSpaceState,
                            onEditCapture = {
                                _uiState.currentEditableWordSpaceLanguageCode = it.wordSpace.language.code
                            },
                            onEditRelease = ::onEditWordSpaceFinish
                        )
                    }
                )
            }

            _uiState.isLoading = false
        }
    }

    private fun onEditWordSpaceFinish() {
        _uiState.currentEditableWordSpaceLanguageCode = null
    }

    private suspend fun onSubmitWordSpaceState(state: LanguageWordSpaceState) {
        // TODO,
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