package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionActions
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionActionsImpl
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionUiState
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsEditModeRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class MDWordDetailsEditModeViewModel @Inject constructor(
    private val repo: MDWordDetailsEditModeRepo,
) : ViewModel() {
    private val _tagsState = MDContextTagsSelectionMutableUiState()
    val contextTagsState: MDContextTagsSelectionUiState = _tagsState
    val contextTagsActions: MDContextTagsSelectionActions = MDContextTagsSelectionActionsImpl(
        state = _tagsState,
        onAddNewTag = ::onAddTagToTree,
        onDeleteTag = ::onRemoveTagFromTree
    )

    private var tagsStateAllTagsStreamCollectorJob: Job? = null

    private val currentLanguageFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val typeTags: StateFlow<List<WordTypeTag>> = currentLanguageFlow.flatMapConcat {
        val language = it ?: ""
        repo.getLanguageTagsStream(language)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState: MDWordDetailsEditModeMutableUiState = MDWordDetailsEditModeMutableUiState(
        tags = _tagsState.selectedTags,
        availableTypeTags = typeTags
    )
    val uiState: MDWordDetailsEditModeUiState = _uiState
    private var lastLoadedWord: Word? = null
    fun initWithNavArgs(args: MDDestination.WordDetailsEditMode) {
        tagsStateAllTagsStreamCollectorJob?.cancel()
        tagsStateAllTagsStreamCollectorJob = viewModelScope.launch {
            repo.getContextTagsStream().collect { allTags ->
                _tagsState.allTagsTree.setFrom(allTags)
                contextTagsActions.refreshCurrentTree()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            currentLanguageFlow.value = args.languageCode
            val id = args.wordId
            if (id == null) {
                _uiState.reset()
            } else {
                val word = repo.getWord(id)
                if (word == null) {
                    _uiState.onExecute { false }
                } else {
                    lastLoadedWord = word
                    _uiState.loadWord(word)
                }
            }
            _uiState.language = args.languageCode.code.language
            _uiState.ensureBlankTrailingField()
        }
    }

    private fun onAddTagToTree(tag: ContextTag) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addOrUpdateContextTag(tag)
        }
    }

    private fun onRemoveTagFromTree(tag: ContextTag) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeContextTag(tag)
        }
    }

    fun getUiActions(
        navActions: MDWordDetailsEditModeNavigationUiActions,
    ): MDWordDetailsEditModeUiActions = MDWordDetailsEditModeUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDWordDetailsEditModeNavigationUiActions,
    ): MDWordDetailsEditModeBusinessUiActions = object : MDWordDetailsEditModeBusinessUiActions {
        override fun onSave() {
            val lastWord = lastLoadedWord
            if (uiState.valid) {
                viewModelScope.launch {
                    _uiState.onExecute {
                        val newWord = Word(
                            meaning = uiState.meaning,
                            translation = uiState.translation,
                            language = uiState.language,
                            additionalTranslations = uiState.additionalTranslations.values.toList().filter { it.isNotBlank() },
                            tags = uiState.tags.toSet(),
                            transcription = uiState.transcription,
                            examples = uiState.examples.values.toList().filter { it.isNotBlank() },
                            wordTypeTag = uiState.selectedTypeTag,
                            relatedWords = uiState.relatedWords.values.mapNotNull {
                                if (it.second.isBlank()) return@mapNotNull null
                                RelatedWord(
                                    id = INVALID_ID,
                                    baseWordId = uiState.id,
                                    relationId = it.first.id,
                                    relationLabel = it.first.label,
                                    value = it.second
                                )
                            },
                            lexicalRelations = uiState.lexicalRelations.mapValues { outer ->
                                outer.value.mapNotNull { inner ->
                                    if (inner.value.isBlank()) return@mapNotNull null
                                    WordLexicalRelation(outer.key, inner.value)
                                }
                            },
                            id = uiState.id,
                            createdAt = lastWord?.createdAt ?: Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                        val newId = if (newWord.id == INVALID_ID) {
                            repo.saveNewWord(newWord)
                        } else {
                            repo.saveExistedWord(newWord)
                            newWord.id
                        }
                        launch(Dispatchers.Main.immediate) {
                            navActions.onNavigateToViewMode(newId, uiState.language.code)
                        }
                        true
                    }
                }
            }
        }

        override fun onEditMeaning(newMeaning: String) {
            _uiState.meaning = newMeaning
        }

        override fun onEditTranslation(newTranslation: String) {
            _uiState.translation = newTranslation
        }

        override fun onEditTranscription(newTranscription: String) {
            _uiState.transcription = newTranscription
        }

        override fun onEditAdditionalTranslations(id: Long, newValue: String) {
            _uiState.additionalTranslations[id] = newValue
//            _uiState.filterBlankAdditionalTranslationsFields(id)
            _uiState.ensureBlankAdditionalTranslationsTrailingField()
        }

        override fun onEditExamples(id: Long, newValue: String) {
            _uiState.examples[id] = newValue
//            _uiState.filterBlankExamplesFields(id)
            _uiState.ensureBlankExamplesTrailingField()
        }

        override fun onEditTypeTag(newTypeTag: WordTypeTag?) {
            _uiState.selectedTypeTag = newTypeTag
//            _uiState.filterBlankRelatedWordsFields()
            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onEditTypeRelationLabel(id: Long, relation: WordTypeTagRelation) {
            val old = _uiState.relatedWords[id]?.second ?: INVALID_TEXT
            _uiState.relatedWords[id] = Pair(relation, old)
//            _uiState.filterBlankRelatedWordsFields(id)
            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onEditTypeRelationValue(id: Long, newValue: String) {
            val old = _uiState.relatedWords[id]?.first ?: _uiState.selectedTypeTag?.relations?.firstOrNull() ?: return
            _uiState.relatedWords[id] = Pair(old, newValue)
//            _uiState.filterBlankRelatedWordsFields(id)
            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onEditLexicalRelation(type: WordLexicalRelationType, id: Long, newValue: String) {
            _uiState.lexicalRelations[type]?.put(id, newValue)
            _uiState.ensureBlankLexicalRelationsTrailingField()
        }

        override fun onFocusChange(newFocused: Long) {
            _uiState.filterBlankFields(newFocused)
            _uiState.ensureBlankTrailingField()
        }

        override fun onAdditionalTranslationsFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankAdditionalTranslationsFields(newFocused)
//            _uiState.ensureBlankAdditionalTranslationsTrailingField()
        }

        override fun onExamplesFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankExamplesFields(newFocused)
//            _uiState.ensureBlankExamplesTrailingField()
        }

        override fun onTypeRelationFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankRelatedWordsFields(newFocused)
//            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onLexicalRelationFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankLexicalRelationsFields(newFocused)
//            _uiState.ensureBlankLexicalRelationsTrailingField()
        }
    }
}
