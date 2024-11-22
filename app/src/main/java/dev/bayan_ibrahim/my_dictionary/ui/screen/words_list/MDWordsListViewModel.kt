package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.code
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val searchQueryDelay = 500L // 0.5 sec

@HiltViewModel
class MDWordsListViewModel @Inject constructor(
    private val repo: MDWordsListRepo,
) : ViewModel() {
    private val _uiState: MDWordsListMutableUiState = MDWordsListMutableUiState()
    val uiState: MDWordsListUiState = _uiState

    private val currentLanguageFlow: Flow<Language?> = repo.getSelectedLanguagePageStream()

    private val viewPreferences = repo.getViewPreferences().onStart {
        this.emit(defaultWordsListViewPreferences)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val wordsList: StateFlow<List<Word>> = currentLanguageFlow.combine(viewPreferences) { language, viewPreferences ->
        language to viewPreferences
    }.flatMapConcat { (language, viewPreferences) ->
        language?.let {
            repo.getWordsList(it.code, viewPreferences)
        } ?: flow {
            emit(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )

    private val selectedLanguagePage = repo.getSelectedLanguagePageStream().onStart {
        this.emit(null)
    }.map {
        _uiState.validData = it != null
        it
    }

    fun initWithNavArgs(args: MDDestination.TopLevel.WordsList) {
        viewModelScope.launch {
            _uiState.onExecute {
                val languageCode = args.languageCode?.code ?: repo.getSelectedLanguagePage()?.code

                if (languageCode != null) {
                    _uiState.selectedWordSpace =
                        repo.getLanguagesWordSpaces(code = languageCode) ?: LanguageWordSpace(languageCode.language)
                    _uiState.preferencesState.onApplyPreferences(viewPreferences.first())
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
            _uiState.isViewPreferencesDialogShown = false
        }

        override fun onShowViewPreferencesDialog() {
            _uiState.isViewPreferencesDialogShown = true
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
                    tag !in uiState.preferencesState.selectedTags && queryRegex.matches(tag.lowercase())
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
                this.selectedLearningProgressGroups = this.selectedLearningProgressGroups.add(group)
            } else {
                this.selectedLearningProgressGroups = this.selectedLearningProgressGroups.remove(group)
            }
        }

        override fun onToggleAllLearningProgressGroups(selected: Boolean) = editViewByPreferences {
            if (selected) {
                this.selectedLearningProgressGroups = WordsListLearningProgressGroup.entries.toPersistentSet()
            } else {
                this.selectedLearningProgressGroups = persistentSetOf()
            }
        }

        override fun onSelectWordsSortBy(sortBy: WordsListSortBy) = editViewByPreferences {
            this.sortBy = sortBy
        }

        override fun onSelectWordsSortByOrder(order: WordsListSortByOrder) = editViewByPreferences {
            this.sortByOrder = order
        }


        override fun onSelectAll() {
            viewModelScope.launch {
                if (uiState.isSelectModeOn) {
                    _uiState.selectedWords = allWordsIds()
                }
            }
        }

        override fun onDeselectAll() {
            if (uiState.isSelectModeOn) {
                _uiState.selectedWords = persistentSetOf()
                _uiState.isSelectModeOn = false
            }
        }

        override fun onInvertSelection() {
            if (uiState.isSelectModeOn) {
                val allWordsIds = allWordsIds()
                _uiState.selectedWords = allWordsIds.removeAll(uiState.selectedWords)
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
    }

    private fun allWordsIds() = wordsList.value.map { it.id }.toPersistentSet()

    private fun onToggleSelectWord(id: Long) {
        val isSelected = id in uiState.selectedWords
        if (isSelected) {
            _uiState.selectedWords = uiState.selectedWords.remove(id)
        } else {
            _uiState.selectedWords = uiState.selectedWords.add(id)
        }
    }

    private fun editViewByPreferences(body: WordsListViewPreferencesMutableState.() -> Unit) {
        _uiState.preferencesState.body()
        viewModelScope.launch {
            launch {
                repo.setViewPreferences(uiState.preferencesState)
            }
        }
    }
}