package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDLanguageSelectionDialogRepo
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDLanguageSelectionDialogViewModel @Inject constructor(
    private val repo: MDLanguageSelectionDialogRepo,
) : ViewModel() {
    private val _uiState: MDLanguageSelectionDialogMutableUiState = MDLanguageSelectionDialogMutableUiState()
    val uiState: MDLanguageSelectionDialogUiState = _uiState
    fun initWithNavArgs() {
        onQueryChange("")
    }

    fun getUiActions(
        navActions: MDLanguageSelectionDialogNavigationUiActions,
    ): MDLanguageSelectionDialogUiActions = MDLanguageSelectionDialogUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )


    private fun getBusinessUiActions(
        navActions: MDLanguageSelectionDialogNavigationUiActions,
    ): MDLanguageSelectionDialogBusinessUiActions = object : MDLanguageSelectionDialogBusinessUiActions {
        override fun onSelectWordSpace(languageWordSpace: LanguageWordSpace) {
            TODO("Not yet implemented")
        }

        override fun onQueryChange(query: String) {
            onQueryChange(query)
        }
    }

    private fun onQueryChange(query: String) {
        _uiState.query = query
        viewModelScope.launch {
            val searchQueryMatchedLanguages = repo.getAllLanguagesWordSpaces().first().run {
                if (query.isBlank()) this
                else filter {
                    it.language.hasMatchQuery(query)
                }
            }

            _uiState.languagesWithWords = searchQueryMatchedLanguages.filter { it.wordsCount > 0 }.toPersistentList()
            _uiState.languagesWithoutWords = searchQueryMatchedLanguages.filter { it.wordsCount == 0 }.toPersistentList()
        }
    }
}
