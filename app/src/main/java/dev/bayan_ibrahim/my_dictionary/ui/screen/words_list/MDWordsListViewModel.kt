package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences.WordsListTrainPreferencesMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences.WordsListViewPreferencesMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainType
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListViewPreferencesSortBy
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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

    fun initWithNavArgs(args: MDDestination.TopLevel.WordsList) {
        setPaginatedWordsListJob()
        viewModelScope.launch {
            _uiState.onExecute {
                val languageCode = args.languageCode?.code ?: repo.getSelectedLanguagePage()?.code

                if (languageCode != null) {
                    _uiState.selectedWordSpace =
                        repo.getLanguagesWordSpaces(code = languageCode) ?: LanguageWordSpace(languageCode.language)
                    _uiState.viewPreferencesState.onApplyPreferences(viewPreferences.first())
                } else {
                    _uiState.selectedWordSpace = LanguageWordSpace()
                    _uiState.validData = false
                }

                // init languages list
                onLanguageWordSpaceSearchQueryChange("")

                true
            }
        }
    }

    private val languagesWordSpacesFlow = repo.getAllLanguagesWordSpaces()

    fun getUiActions(
        navActions: MDWordsListNavigationUiActions,
    ) = MDWordsListUiActions(
        navigationActions = navActions,
        businessActions = getBusinessActions(navActions)
    )

    private fun onLanguageWordSpaceSearchQueryChange(searchQuery: String) {
        _uiState.languagesWordSpaceSearchQuery = searchQuery
        viewModelScope.launch {
            val searchQueryMatchedLanguages = languagesWordSpacesFlow.first().run {
                Log.d("language", "all word spaces $this")
                if (searchQuery.isBlank()) this
                else filter {
                    it.language.hasMatchQuery(searchQuery)
                }
            }

            Log.d("language", "all word spaces matches ($searchQuery) $searchQueryMatchedLanguages")
            _uiState.activeLanguagesWordSpaces = searchQueryMatchedLanguages.filter { it.wordsCount > 0 }.toPersistentList()
            _uiState.inactiveLanguagesWordSpaces = searchQueryMatchedLanguages.filter { it.wordsCount == 0 }.toPersistentList()
        }
    }

    private fun getBusinessActions(
        navActions: MDWordsListNavigationUiActions,
    ): MDWordsListBusinessUiActions = object : MDWordsListBusinessUiActions {
        override fun onLanguageWordSpaceSearchQueryChange(searchQuery: String) {
            this@MDWordsListViewModel.onLanguageWordSpaceSearchQueryChange(searchQuery)
        }

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

        override fun onClearViewPreferences() = editViewByPreferences {
            this.onApplyPreferences(defaultWordsListViewPreferences)
        }

        override fun onHideViewPreferencesDialog() {
            _uiState.viewPreferencesState.showDialog = false
        }

        override fun onShowViewPreferencesDialog() {
            _uiState.viewPreferencesState.showDialog = true
        }

        override fun onSearchQueryChange(query: String) = editViewByPreferences {
            this.searchQuery = query
        }

        override fun onSelectSearchTarget(searchTarget: WordsListSearchTarget) = editViewByPreferences {
            this.searchTarget = searchTarget
        }

        override fun onTagSearchQueryChange(query: String) {
            _uiState.tagSearchQuery = query
            viewModelScope.launch {
                // todo normalize this search method
                val queryRegex = query.lowercase().toCharArray().joinToString(".*", ".*", ".*").toRegex()
                val newTags = repo.getLanguageTags(uiState.selectedWordSpace.language.code).first().filter { tag ->
                    tag !in uiState.viewPreferencesState.selectedTags && queryRegex.matches(tag.lowercase())
                }
                _uiState.tagsSuggestions.clear()
                _uiState.tagsSuggestions.addAll(newTags)
            }
        }

        override fun onSelectTag(tag: String) = editViewByPreferences {
            this.selectedTags.add(tag)
            _uiState.tagSearchQuery = ""
        }

        override fun onRemoveTag(tag: String) = editViewByPreferences {
            this.selectedTags.remove(tag)
        }

        override fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean) = editViewByPreferences {
            this.includeSelectedTags = includeSelectedTags
        }

        override fun onSelectLearningGroup(
            group: WordsListLearningProgressGroup,
        ) = editViewByPreferences {
            if (group in selectedLearningProgressGroups) {
                this.selectedLearningProgressGroups = this.selectedLearningProgressGroups.remove(group)
            } else {
                this.selectedLearningProgressGroups = this.selectedLearningProgressGroups.add(group)
            }
        }

        override fun onToggleAllLearningProgressGroups(selected: Boolean) = editViewByPreferences {
            if (selected) {
                this.selectedLearningProgressGroups = WordsListLearningProgressGroup.entries.toPersistentSet()
            } else {
                this.selectedLearningProgressGroups = persistentSetOf()
            }
        }

        override fun onSelectWordsSortBy(sortBy: WordsListViewPreferencesSortBy) = editViewByPreferences {
            this.sortBy = sortBy
        }

        override fun onSelectWordsSortByOrder(order: WordsListSortByOrder) = editViewByPreferences {
            this.sortByOrder = order
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

        override fun onShowTrainPreferencesDialog() {
            _uiState.trainPreferencesState.showDialog = true
        }

        override fun onSelectTrainType(trainType: WordsListTrainType) = editTrainPreferences {
            this.trainType = trainType
        }

        override fun onSelectTrainTarget(trainTarget: WordsListTrainTarget) = editTrainPreferences {
            this.trainTarget = trainTarget
        }

        override fun onSelectLimit(limit: WordsListTrainPreferencesLimit) = editTrainPreferences {
            this.limit = limit
        }

        override fun onSelectSortBy(sortBy: WordsListTrainPreferencesSortBy) = editTrainPreferences {
            this.sortBy = sortBy
        }

        override fun onSelectSortByOrder(sortByOrder: WordsListSortByOrder) = editTrainPreferences {
            this.sortByOrder = sortByOrder
        }

        override fun onHideTrainPreferencesDialog() {
            _uiState.trainPreferencesState.showDialog = false
        }

        override fun onConfirmTrain() {
            // TODO
            
        }

        override fun onResetTrainPreferences() = editTrainPreferences {
            this.onApplyPreferences(defaultWordsListTrainPreferences)
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

    private fun editViewByPreferences(body: WordsListViewPreferencesMutableState.() -> Unit) {
        _uiState.viewPreferencesState.body()
        viewModelScope.launch {
            launch {
                repo.setViewPreferences(uiState.viewPreferencesState)
            }
        }
    }

    private fun editTrainPreferences(body: WordsListTrainPreferencesMutableState.() -> Unit) {
        _uiState.trainPreferencesState.body()
        viewModelScope.launch {
            launch {
                repo.setTrainPreferences(uiState.trainPreferencesState)
            }
        }
    }
}