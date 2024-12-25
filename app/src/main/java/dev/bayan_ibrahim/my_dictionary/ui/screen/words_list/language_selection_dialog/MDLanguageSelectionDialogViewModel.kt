package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDLanguageSelectionDialogRepo
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDLanguageSelectionDialogViewModel @Inject constructor(
    private val repo: MDLanguageSelectionDialogRepo,
) : ViewModel() {
    private val _uiState: MDLanguageSelectionDialogMutableUiState = MDLanguageSelectionDialogMutableUiState()
    val uiState: MDLanguageSelectionDialogUiState = _uiState
    private val languageFlow = repo.getSelectedLanguagePageStream()
    private var languageFlowListener: Job? = null
    fun initWithNavArgs() {
        languageFlowListener?.cancel()
        languageFlowListener = viewModelScope.launch {
            languageFlow.collect { language ->
                val currentLanguage = repo.getSelectedLanguagePage() ?: return@collect
                val currentWordSpace = repo.getLanguagesWordSpaces(currentLanguage.code) ?: return@collect
                _uiState.selectedWordSpace = currentWordSpace
                onLanguageQueryChange(uiState.query)
            }
        }
        viewModelScope.launch {
            val currentLanguage = repo.getSelectedLanguagePage() ?: return@launch
            val currentWordSpace = repo.getLanguagesWordSpaces(currentLanguage.code) ?: return@launch
            _uiState.selectedWordSpace = currentWordSpace
            onLanguageQueryChange("")
        }
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
            _uiState.selectedWordSpace = languageWordSpace
            viewModelScope.launch {
                repo.setSelectedLanguagePage(languageWordSpace.language.code)
            }
        }

        override fun onQueryChange(query: String) {
            onLanguageQueryChange(query)
        }
    }

    private fun onLanguageQueryChange(query: String) {
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
