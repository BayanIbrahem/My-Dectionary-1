package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.toMDEditableField
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordSpaceViewModel @Inject constructor(
    private val repo: MDWordSpaceRepo,
) : ViewModel() {
    private val _uiState: MDWordSpaceMutableUiState = MDWordSpaceMutableUiState()
    val uiState: MDWordSpaceUiState = _uiState

    fun initWithNavArgs(args: MDDestination.TopLevel.WordSpace) {
        viewModelScope.launch(Dispatchers.IO) {
                _uiState.onExecute {
                    repo.getLanguagesWordSpacesWithTags().forEach { (wordSpace, tags) ->
                        _uiState.wordSpacesWithActions.clear()
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
                    true
            }
        }
    }

    private fun onEditWordSpaceFinish() {
        _uiState.currentEditableWordSpaceLanguageCode = null
    }

    private suspend fun onSubmitWordSpaceState(state: LanguageWordSpaceState) {
        repo.editLanguageTags(
            code = state.wordSpace.language.code,
            tags = state.tags.map { it.current }
        )
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
        override fun onAddNewWordSpace(code: LanguageCode) {
            viewModelScope.launch {
                _uiState.onExecute {
                    val added  =repo.addNewWordSpace(code.code)
                    if (added) {
                        _uiState.wordSpacesWithActions.add(
                            LanguageWordSpaceMutableState(
                                wordSpace = LanguageWordSpace(code.language),
                                initialTags = emptyList()
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
                    true
                }
            }
        }
    }
}