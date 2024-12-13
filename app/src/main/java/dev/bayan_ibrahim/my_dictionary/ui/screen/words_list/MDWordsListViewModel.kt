package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordsListViewModel @Inject constructor(
    private val repo: MDWordsListRepo,
) : ViewModel() {
    private val _uiState: MDWordsListMutableUiState = MDWordsListMutableUiState()
    val uiState: MDWordsListUiState = _uiState

    private val currentLanguageFlow: Flow<Language?> = repo.getSelectedLanguagePageStream().map {
        it.also {
            _uiState.validData = it != null
        }
    }

    private val viewPreferences = repo.getViewPreferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = defaultWordsListViewPreferences
    )

    private val _paginatedWordsList: MutableStateFlow<PagingData<Word>> = MutableStateFlow(PagingData.empty())
    val paginatedWordsList: StateFlow<PagingData<Word>> = _paginatedWordsList.asStateFlow()

    private var paginatedWordsListJob: Job? = null
    private fun setPaginatedWordsListJob() {
        viewModelScope.launch {
            paginatedWordsListJob?.cancel()
            viewPreferences.collectLatest { preferences ->
                updateUiStateFromViewPreferences(preferences)
                currentLanguageFlow.collectLatest { language ->
                    paginatedWordsListJob = launch {
                        val wordsIds = repo.getWordsIdsOfTagsAndProgressRange(preferences)
                        repo.getPaginatedWordsList(
                            code = language?.code ?: "".code,
                            wordsIdsOfTagsAndProgressRange = wordsIds,
                            viewPreferences = preferences,
                        ).distinctUntilChanged()
                            .cachedIn(viewModelScope)
                            .collect { pagingData ->
                                _paginatedWordsList.value = pagingData
                            }
                    }
                }
            }
        }
    }

    private fun updateUiStateFromViewPreferences(viewPreferences: MDWordsListViewPreferences) {
        _uiState.isViewPreferencesEffectiveFilter = viewPreferences.effectiveFilter

        val query = viewPreferences.searchQuery.nullIfInvalid()
        _uiState.viewPreferencesQuery = when (viewPreferences.searchTarget) {
            MDWordsListSearchTarget.Meaning -> query to null
            MDWordsListSearchTarget.Translation -> null to query
            MDWordsListSearchTarget.All -> query to query
        }
    }

    fun initWithNavArgs(args: MDDestination.TopLevel.WordsList) {
        setPaginatedWordsListJob()
        viewModelScope.launch {
            _uiState.onExecute {
                repo.getViewPreferences()
                val languageCode = args.languageCode?.code ?: repo.getSelectedLanguagePage()?.code

                if (languageCode != null) {
                    _uiState.selectedWordSpace =
                        repo.getLanguagesWordSpaces(code = languageCode) ?: LanguageWordSpace(languageCode.language)
                    repo.setSelectedLanguagePage(languageCode)
                } else {
                    _uiState.selectedWordSpace = LanguageWordSpace()
                    _uiState.validData = false
                }

                // init languages list
//                onLanguageWordSpaceSearchQueryChange("")

                true
            }
        }
    }

    fun getUiActions(
        navActions: MDWordsListNavigationUiActions,
    ) = MDWordsListUiActions(
        navigationActions = navActions,
        businessActions = getBusinessActions(navActions)
    )


    private fun getBusinessActions(
        navActions: MDWordsListNavigationUiActions,
    ): MDWordsListBusinessUiActions = object : MDWordsListBusinessUiActions {

        override fun onShowLanguageWordSpacesDialog() {
            _uiState.isLanguagesWordSpacesDialogShown = true
        }

        override fun onHideLanguageWordSpacesDialog() {
            _uiState.isLanguagesWordSpacesDialogShown = false
        }

        override fun onSelectLanguageWordSpace(wordSpace: LanguageWordSpace) {
            _uiState.selectedWordSpace = wordSpace

            viewModelScope.launch {
                launch {
                    repo.setSelectedLanguagePage(wordSpace.language.code)
                }
            }
        }

        override fun onDeleteLanguageWordSpace() {
            _uiState.isLanguageWordSpaceDeleteDialogShown = true
        }

        override fun onConfirmDeleteLanguageWordSpace() {
            _uiState.isLanguageWordSpaceDeleteProcessRunning = true
            // todo, delete
            _uiState.isLanguageWordSpaceDeleteProcessRunning = false
            _uiState.isLanguageWordSpaceDeleteDialogShown = false
        }

        override fun onCancelDeleteLanguageWordSpace() {
            _uiState.isLanguageWordSpaceDeleteDialogShown = false
        }

        override fun onClickWord(id: Long) {
            if (uiState.isSelectModeOn) {
                onToggleSelectWord(id)
            } else {
                navActions.navigateToWordDetails(id)
            }
        }

        override fun onLongClickWord(id: Long) {
            _uiState.isSelectModeOn = true
            onToggleSelectWord(id)
        }

        override fun onAddNewWord() {
            navActions.navigateToWordDetails(null)
        }


//        override fun onSelectAll() {
//            viewModelScope.launch {
//                if (uiState.isSelectModeOn) {
//                    _uiState.selectedWords = allWordsIds()
//                }
//            }
//        }

        override fun onDeselectAll() {
            if (uiState.isSelectModeOn) {
                _uiState.selectedWords = persistentSetOf()
                _uiState.isSelectModeOn = false
            }
        }

        //        override fun onInvertSelection() {
//            if (uiState.isSelectModeOn) {
//                val allWordsIds = allWordsIds()
//                _uiState.selectedWords = allWordsIds.removeAll(uiState.selectedWords)
//            }
//        }
//
        override fun onDeleteSelection() {
            _uiState.isSelectedWordsDeleteDialogShown = true
        }

        override fun onConfirmDeleteSelection() {
            if (uiState.isSelectModeOn) {
                viewModelScope.launch {
                    _uiState.onExecute {
                        val ids = uiState.selectedWords

                        repo.deleteWords(ids)

                        _uiState.isSelectedWordsDeleteDialogShown = false
                        _uiState.isSelectModeOn = false
                        true
                    }
                }
            }
        }

        override fun onCancelDeleteSelection() {
            _uiState.isSelectedWordsDeleteDialogShown = false
        }

        override fun onClearSelection() {
            _uiState.isSelectModeOn = false
            _uiState.selectedWords = persistentSetOf()
        }

        override fun onShowTrainDialog() {
            _uiState.showTrainPreferencesDialog = true
        }

        override fun onDismissTrainDialog() {
            _uiState.showTrainPreferencesDialog = false
        }

        override fun onShowViewPreferencesDialog() {
            _uiState.showViewPreferencesDialog = true
        }

        override fun onDismissViewPreferencesDialog() {
            _uiState.showViewPreferencesDialog = false
        }
    }

    private fun onToggleSelectWord(id: Long) {
        val isSelected = id in uiState.selectedWords
        if (isSelected) {
            _uiState.selectedWords = uiState.selectedWords.remove(id)
        } else {
            _uiState.selectedWords = uiState.selectedWords.add(id)
        }
    }

}