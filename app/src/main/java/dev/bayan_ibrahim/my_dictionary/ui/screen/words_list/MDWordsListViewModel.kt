package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech.TextToSpeechData
import dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech.TextToSpeechDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferencesBuilder
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.defaultLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.LanguageRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.ViewPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordsListViewModel @Inject constructor(
    private val userRepo: UserPreferencesRepo,
    private val languageRepo: LanguageRepo,
    private val wordRepo: WordRepo,
    private val textToSpeech: TextToSpeechDataSource,
    private val  viewPreferencesRepo: ViewPreferencesRepo,
) : ViewModel() {
    private val selectedWordSpaceStream: StateFlow<LanguageWordSpace> = userRepo.getUserPreferencesStream().map {
        val language = it.selectedLanguagePage ?: let {
            val defaultLanguage = defaultLanguage
            userRepo.setUserPreferences { userPreferences ->
                userPreferences.copy(selectedLanguagePage = defaultLanguage)
            }
            defaultLanguage
        }
        languageRepo.getLanguageWordSpace(language) ?: LanguageWordSpace(language.code)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LanguageWordSpace(defaultLanguage.code))
    private val _uiState: MDWordsListMutableUiState = MDWordsListMutableUiState(selectedWordSpaceStream)
    val uiState: MDWordsListUiState = _uiState

    val currentSpeakingWordId = textToSpeech.runningId

    private val viewPreferences = viewPreferencesRepo.getViewPreferencesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = defaultWordsListViewPreferences
    )
    val searchQueryFlow = viewPreferences.map {
        it.searchQuery
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    private val _paginatedWordsList: MutableStateFlow<PagingData<Word>> = MutableStateFlow(PagingData.empty())
    val paginatedWordsList: StateFlow<PagingData<Word>> = _paginatedWordsList.asStateFlow()
    val lifeMemorizingProbability = userRepo.getUserPreferencesStream().map {
        it.liveMemorizingProbability
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    private var paginatedWordsListJob: Job? = null

    private fun setPaginatedWordsListJob() {
        viewModelScope.launch {
            paginatedWordsListJob?.cancel()
            viewPreferences.collectLatest { preferences ->
                updateUiStateFromViewPreferences(preferences)
                selectedWordSpaceStream.collectLatest { language ->
                    paginatedWordsListJob = launch {
                        val targetWords = wordRepo.getWordsIdsOf(
                            languages = setOf(language),
                            contextTags = preferences.selectedTags,
                            includeContextTags = preferences.includeSelectedTags,
                            memorizingProbabilities = preferences.selectedMemorizingProbabilityGroups
                        ).first()
                        wordRepo.getPaginatedWordsList(
                            code = language,
                            targetWords = targetWords,
                            includeMeaning = preferences.searchTarget.includeMeaning,
                            includeTranslation = preferences.searchTarget.includeTranslation,
                            searchQuery = preferences.searchQuery,
                            sortBy = preferences.sortBy,
                            sortByOrder = preferences.sortByOrder,
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

    fun initWithNavArgs(
        args: MDDestination.TopLevel.WordsList,
    ) {
        setPaginatedWordsListJob()
        viewModelScope.launch {
            _uiState.onExecute {
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
            viewModelScope.launch {
                launch {
                    userRepo.setUserPreferences {
                        it.copy(selectedLanguagePage = wordSpace)
                    }
                }
            }
        }

        override fun onDeleteLanguageWordSpace() {
            _uiState.isLanguageWordSpaceDeleteDialogShown = true
        }

        override fun onConfirmDeleteLanguageWordSpace() {
            viewModelScope.launch {
                _uiState.isLanguageWordSpaceDeleteProcessRunning = true
                languageRepo.deleteWordSpace(uiState.selectedWordSpace.value)
                _uiState.isLanguageWordSpaceDeleteProcessRunning = false
                _uiState.isLanguageWordSpaceDeleteDialogShown = false
                initWithNavArgs(MDDestination.TopLevel.WordsList())
            }
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

        override fun onDeselectAll() {
            if (uiState.isSelectModeOn) {
                _uiState.selectedWords = persistentSetOf()
                _uiState.isSelectModeOn = false
            }
        }

        override fun onDeleteSelection() {
            _uiState.isSelectedWordsDeleteDialogShown = true
        }

        override fun onConfirmDeleteSelection() {
            if (uiState.isSelectModeOn) {
                viewModelScope.launch {
                    _uiState.onExecute {
                        val ids = uiState.selectedWords

                        wordRepo.deleteWords(ids)

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

        override fun onConfirmAppendContextTagsOnSelectedWords(selectedTags: List<ContextTag>) {
            if (uiState.isSelectModeOn) {
                if (uiState.selectedWords.isNotEmpty()) {
                    if (selectedTags.isNotEmpty()) {
                        viewModelScope.launch {
                            wordRepo.appendTagsToWords(
                                wordsIds = uiState.selectedWords,
                                tags = selectedTags
                            )
                        }
                    }
                }
            }
        }
        override fun onSearchQueryChange(query :String ){
            viewModelScope.launch {
                viewPreferencesRepo.setViewPreferences {
                    WordsListViewPreferencesBuilder(it).copy(searchQuery = query)
                }
            }
        }

        override fun onSpeakWord(word: Word) {
            if (currentSpeakingWordId.value != word.id.toString()) {
                textToSpeech.pushData(
                    TextToSpeechData(
                        id = word.id.toString(),
                        text = word.meaning,
                        language = word.language,
                        flushPrev = true
                    )
                )
            }
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