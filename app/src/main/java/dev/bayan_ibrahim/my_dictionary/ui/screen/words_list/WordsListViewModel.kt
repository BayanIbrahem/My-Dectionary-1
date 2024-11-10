package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordsListRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val searchQueryDelay = 500L // 0.5 sec

@HiltViewModel
class WordsListViewModel @Inject constructor(
    private val repo: WordsListRepo,
) : ViewModel() {
    private val _uiState: WordsListMutableUiState = WordsListMutableUiState()
    val uiState: WordsListUiState = _uiState

    private val viewPreferences = repo.getViewPreferences().onStart {
        this.emit(defaultWordsListViewPreferences)
    }
    private val selectedLanguagePage = repo.getSelectedLanguagePageStream().onStart {
        this.emit(null)
    }.onEach {
        _uiState.validData = it != null
    }

    fun initWithNavArgs(args: MDDestination.TopLevel.WordsList) {
        viewModelScope.launch {
            _uiState.onExecute {
                val languageCode = args.languageCode ?: repo.getSelectedLanguagePage()?.code

                if (languageCode != null) {
                    _uiState.selectedWordSpace =
                        repo.getLanguagesWordSpaces(languageCode = languageCode) ?: allLanguages[languageCode]!!.let { language ->
                            LanguageWordSpace(language)
                        }

                    _uiState.preferencesState.onApplyPreferences(viewPreferences.first())

                    syncWordsList()
                }
                true
            }
        }
    }

    private val languageSearchQueryFlow: MutableStateFlow<String> = MutableStateFlow(INVALID_TEXT)
    private val languagesWordSpacesFlow = repo.getAllLanguagesWordSpaces()
        .onStart {
            emptyList<LanguageWordSpace>()
        }.combine(
            languageSearchQueryFlow.onStart { INVALID_TEXT }
        ) { wordSpaces, searchQuery ->
            if (searchQuery.isBlank()) {
                wordSpaces
            } else {
                wordSpaces.filter { it.language.hasMatchQuery(searchQuery) }
            }
        }.flowOn(Dispatchers.Default).onEach {
            _uiState.activeLanguagesWordSpaces = it.filter { it.wordsCount > 0 }.toPersistentList()
            _uiState.inactiveLanguagesWordSpaces = it.filter { it.wordsCount == 0 }.toPersistentList()
        }

    fun getUiActions(
        navActions: WordsListNavigationUiActions,
    ) = WordsListUiActions(
        navigationActions = navActions,
        businessActions = getBusinessActions(navActions)
    )

    // TODO, check selection actions for top app bar

    private fun getBusinessActions(
        navActions: WordsListNavigationUiActions,
    ): WordsListBusinessUiActions = object : WordsListBusinessUiActions {
        override fun onLanguageWordSpaceSearchQueryChange(searchQuery: String) {
            _uiState.languagesWordSpaceSearchQuery = searchQuery
            languageSearchQueryFlow.value = searchQuery
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
                    syncWordsList()
                }
                launch {
                    repo.setSelectedLanguagePage(wordSpace.language.code)
                }
            }
        }

        override fun onDeleteLanguageWordSpace() {
            val currentWordSpace = uiState.activeLanguagesWordSpaces
            TODO("Not yet implemented")
        }

        override fun onConfirmDeleteLanguageWordSpace() {
            TODO("Not yet implemented")
        }

        override fun onCancelDeleteLanguageWordSpace() {
            TODO("Not yet implemented")
        }

        override fun onClickWord(id: Long) {
            if (uiState.isSelectModeOn) {
                onToggleSelectWord(id)
            } else {
                navActions.navigateToWordDetails(id)
            }
        }

        override fun onLongClickWord(id: Long) {
            if (uiState.isSelectModeOn) {
                onToggleSelectWord(id)
            } else {
                _uiState.isSelectModeOn = true
            }
        }

        override fun onAddNewWord() {
            navActions.navigateToWordDetails(null)
        }

        override fun onClearViewPreferences() = editViewByPreferences {
            this.onApplyPreferences(defaultWordsListViewPreferences)
        }

        override fun onSearchQueryChange(query: String) = editViewByPreferences {
            this.searchQuery = query
        }

        override fun onToggleTagFilter(tag: String, select: Boolean) = editViewByPreferences {
            if (select) {
                this.selectedTags = this.selectedTags.add(tag)
            } else {
                this.selectedTags = this.selectedTags.remove(tag)
            }
        }

        override fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean) = editViewByPreferences {
            this.includeSelectedTags = includeSelectedTags
        }

        override fun onToggleLearningProgressGroup(
            group: WordsListLearningProgressGroup,
            selected: Boolean,
        ) = editViewByPreferences {
            if (selected) {
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
            if (uiState.isSelectModeOn && uiState.isSelectedWordsDeleteDialogShown) {
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

    private fun allWordsIds() = uiState.words.map { it.id }.toPersistentSet()

    private fun onToggleSelectWord(id: Long) {
        val isSelected = id in uiState.selectedWords
        if (isSelected) {
            _uiState.selectedWords = uiState.selectedWords.remove(id)
        } else {
            _uiState.selectedWords = uiState.selectedWords.add(id)
        }
    }

    private suspend fun syncWordsList() {
        _uiState.onExecute {
            val preferences = viewPreferences.first()
            val newWords = repo.getWordsList(uiState.selectedWordSpace.language.code, preferences).firstOrNull() ?: emptyList()
            _uiState.words.clear()
            _uiState.words.addAll(newWords)
            true
        }
    }

    private fun editViewByPreferences(body: WordsListViewPreferencesMutableState.() -> Unit) {
        _uiState.preferencesState.body()
        viewModelScope.launch {
            launch {
                syncWordsList()
            }
            launch {
                repo.setViewPreferences(uiState.preferencesState)
            }
        }
    }
}