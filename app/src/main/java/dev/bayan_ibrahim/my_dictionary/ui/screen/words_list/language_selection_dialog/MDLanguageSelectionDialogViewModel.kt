package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.repo.LanguageRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDLanguageSelectionDialogViewModel @Inject constructor(
    private val userRepo: UserPreferencesRepo,
    private val languageRepo: LanguageRepo,
) : ViewModel() {
    private val languagesWordSpacesStream = languageRepo.getAllLanguagesWordSpaces(
        includeNotUsedLanguages = true
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val searchQueryStream = MutableStateFlow("")
    private val selectedWordSpaceStream = userRepo.getUserPreferencesStream().map {
        it.selectedLanguagePage?.let { language ->
            languageRepo.getLanguageWordSpace(language)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )


    val uiState: StateFlow<MDLanguageSelectionDialogUiState> = combine(
        languagesWordSpacesStream,
        selectedWordSpaceStream,
        searchQueryStream
    ) { wordSpaces, selectedLanguage, query ->
        val selectedWordSpace = wordSpaces.firstNotNullOfOrNull { if (it.code == selectedLanguage?.code) it else null }
        val groupedSpaces = wordSpaces.filter {
            query.isBlank() || it.hasMatchQuery(query)
        }.groupBy {
            it.wordsCount > 0
        }
        MDLanguageSelectionDialogUiState.Data(
            selectedWordSpace = selectedWordSpace,
            query = query,
            languagesWithWords = groupedSpaces[true] ?: emptyList(),
            languagesWithoutWords = groupedSpaces[false] ?: emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MDLanguageSelectionDialogUiState.Loading,
    )

    fun initWithNavArgs() {}

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
            viewModelScope.launch {
                userRepo.setUserPreferences {
                    it.copy(selectedLanguagePage = languageWordSpace)
                }
            }
        }

        override fun onQueryChange(query: String) {
            searchQueryStream.value = query
        }
    }
}
