package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.toMDEditableField
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.repo.LanguageRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TypeTagRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordSpaceViewModel @Inject constructor(
    private val languageRepo: LanguageRepo,
    private val typeTagRepo: TypeTagRepo,
) : ViewModel() {
    private val _uiState: MDWordSpaceMutableUiState = MDWordSpaceMutableUiState()
    val uiState: MDWordSpaceUiState = _uiState

    fun initWithNavArgs(args: MDDestination.TopLevel.WordSpace) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.onExecute {
                _uiState.wordSpacesWithActions.clear()
                val allWordSpaces = languageRepo.getAllLanguagesWordSpaces(false).first().associate {
                    (it as LanguageCode) to it.wordsCount
                }
                typeTagRepo.getAllTypeTags().first().forEach { (language, tags) ->
                    _uiState.wordSpacesWithActions.add(
                        buildWordSpaceStateWithActions(
                            language = language,
                            tags = tags,
                            wordsCount = allWordSpaces[language] ?: 0,
                        )
                    )
                }
                val languagesWithoutTypeTags: List<LanguageCode> = allWordSpaces.mapNotNull { (language) ->
                    if (uiState.wordSpacesWithActions.any { it.first.code == language.code }) {
                        null
                    } else {
                        language
                    }
                }
                _uiState.wordSpacesWithActions.addAll(
                    languagesWithoutTypeTags.map { language ->
                        buildWordSpaceStateWithActions(
                            language = language,
                            tags = emptyList(),
                            wordsCount = allWordSpaces[language] ?: 0,
                        )
                    }
                )
                true
            }
        }
    }

    private fun buildWordSpaceStateWithActions(
        language: LanguageCode,
        tags: List<WordTypeTag>,
        wordsCount: Int,
    ) = LanguageWordSpaceMutableState(
        code = language.code,
        initialTags = tags.map { it.toMDEditableField(false) },
        wordsCount = wordsCount,
    ).let {
        it to it.getActions(
            scope = viewModelScope,
            onSubmitRequest = ::onSubmitWordSpaceState,
            onEditCapture = {
                _uiState.currentEditableWordSpaceLanguageCode = it
            },
            onEditRelease = ::onEditWordSpaceFinish
        )
    }

    private fun onEditWordSpaceFinish() {
        _uiState.currentEditableWordSpaceLanguageCode = null
    }

    private suspend fun onSubmitWordSpaceState(state: LanguageWordSpaceState) {
        typeTagRepo.setLanguageTypeTags(
            code = state,
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
                    val added = languageRepo.insertLanguage(code)
                    if (added) {
                        _uiState.wordSpacesWithActions.add(
                            LanguageWordSpaceMutableState(
                                code = code.code,
                                initialTags = emptyList()
                            ).let {
                                it to it.getActions(
                                    scope = viewModelScope,
                                    onSubmitRequest = ::onSubmitWordSpaceState,
                                    onEditCapture = {
                                        _uiState.currentEditableWordSpaceLanguageCode = it
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