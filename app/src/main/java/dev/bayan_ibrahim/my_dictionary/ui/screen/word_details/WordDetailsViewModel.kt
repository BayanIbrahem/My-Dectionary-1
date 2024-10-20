package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordDetailsRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    private val repo: WordDetailsRepo,
) : ViewModel() {
    private val _uiState: WordDetailsMutableUiState = WordDetailsMutableUiState()
    val uiState: WordDetailsUiState get() = _uiState

    fun initWithNavigationArgs(args: MDDestination.WordDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.onExecute {
                args.wordId?.let { id ->
                    val word = repo.getWord(id)
                    _uiState.loadWord(word)
                }
                val tags = repo.getLanguageTags(args.languageCode)
                    .ifEmpty {
                        listOf(
                            WordTypeTag(
                                id = INVALID_ID,
                                name = "dummy_type_tag",
                                language = Language("en", "English", "English"),
                                relations = listOf(
                                    "relation1",
                                    "relation2"
                                )
                            )
                        )
                    }
                _uiState.typeTags.setAll(tags)
                _uiState.selectedTypeTag = tags.firstOrNull()
                // if it is a new word then it should be the on edit mode initially otherwise it should be on reading mode
                _uiState.isEditModeOn = args.wordId == null

                _uiState.ensureOnTrailingBlankItemAdditionalTranslation()
                _uiState.ensureOnTrailingBlankItemTag()
                _uiState.ensureOnTrailingBlankItemExample()
                _uiState.ensureOnTrailingBlankItemTag()
                _uiState.ensureOnTrailingBlankItemRelatedWord()

                true
            }
        }
    }

    private fun ensureEditableUiState(scope: WordDetailsMutableUiState.() -> Unit) {
        viewModelScope.launch {
            if (!_uiState.isLoading && _uiState.isEditModeOn) {
                _uiState.scope()
            }
        }
    }

    fun getUiActions(
        navigationActions: WordDetailsNavigationUiActions,
    ): WordDetailsBusinessUiActions = object : WordDetailsBusinessUiActions {
        override fun onEnableEditMode() {
            if (!_uiState.isLoading) {
                _uiState.isEditModeOn = true
            }
        }

        override fun onSaveChanges() = ensureEditableUiState {
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.onExecute {
                    _uiState.isEditModeOn = false
                    onValidateAdditionalTranslations()
                    onValidateTags()
                    onValidateRelatedWords()
                    onValidateExamples()
                    val word = _uiState.toWord()
                    if (word.id == INVALID_ID) {
                        val newWord = repo.saveNewWord(word)
                        // we can import the whole word but this is faster
                        _uiState.id = newWord.id
                    } else {
                        repo.saveExistedWord(word)
                    }
                    true
                }
            }
        }

        override fun onCancelChanges() = ensureEditableUiState {
            if (_uiState.id == INVALID_ID) {
                navigationActions.pop()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.onExecute {
                        val navArgs = MDDestination.WordDetails(
                            wordId = _uiState.id,
                            languageCode = _uiState.language.code
                        )
                        initWithNavigationArgs(navArgs)
                        _uiState.isEditModeOn = false
                        true
                    }
                }
            }
        }

        override fun onMeaningChange(newMeaning: String) = ensureEditableUiState {
            meaning = newMeaning
            validateWord()
        }

        override fun onTranscriptionChange(newTranscription: String) = ensureEditableUiState {
            transcription = newTranscription
        }

        override fun onTranslationChange(newTranslation: String) = ensureEditableUiState {
            translation = newTranslation
            validateWord()
        }

        override fun onEditAdditionalTranslation(id: Long, newAdditionalTranslation: String) = ensureEditableUiState {
            additionalTranslations[id] = newAdditionalTranslation
        }

        override fun onValidateAdditionalTranslations(focusedTextFieldId: Long?) = _uiState.additionalTranslations.validateField(
            focusedTextFieldId = focusedTextFieldId,
            onAdd = _uiState::addAdditionalTranslation
        )

        override fun onEditTag(id: Long, newTag: String) = ensureEditableUiState {
            tags[id] = newTag
        }

        override fun onValidateTags(focusedTextFieldId: Long?) = _uiState.tags.validateField(
            focusedTextFieldId = focusedTextFieldId,
            onAdd = _uiState::addTag
        )

        override fun onChangeTypeTag(newTypeTag: WordTypeTag?) = ensureEditableUiState {
            this.selectedTypeTag = newTypeTag
            this.ensureOnTrailingBlankItemRelatedWord()
        }

        override fun onAddNewRelatedWord(relation: String) = ensureEditableUiState {
            addRelatedWord(relation, INVALID_TEXT)
        }

        override fun onEditRelatedWordRelation(id: Long, newRelation: String) = ensureEditableUiState {
            val oldValue = relatedWords[id]?.second ?: INVALID_TEXT
            relatedWords[id] = newRelation to oldValue
        }

        override fun onEditRelatedWordValue(id: Long, newValue: String) = ensureEditableUiState {
            val oldRelation = relatedWords[id]?.first ?: return@ensureEditableUiState
            relatedWords[id] = oldRelation to newValue
        }

        override fun onRemoveRelatedWord(id: Long, relation: String, value: String) {
            _uiState.relatedWords.remove(id)
        }

        override fun onValidateRelatedWords(focusedTextFieldId: Long?) {
            _uiState.relatedWords.run {
                viewModelScope.launch {
                    val typeRelations = _uiState.selectedTypeTag?.relations?.toSet()

                    if (typeRelations.isNullOrEmpty()) return@launch

                    val latestId = maxOf { it.key } // latest id
                    forEach { (id, relation) ->
                        if (id != focusedTextFieldId && id != latestId && (relation.first !in typeRelations || relation.second.isBlank())) {
                            remove(id)
                        }
                    }
                    if (this@run[latestId]?.let { (it.first !in typeRelations || it.second.isBlank()) } != true) {
                        _uiState.addRelatedWord(typeRelations.first(), INVALID_TEXT)
                    }
                }
            }
        }

        override fun onEditExample(id: Long, newExample: String) = ensureEditableUiState {
            examples[id] = newExample
        }

        override fun onValidateExamples(focusedTextFieldId: Long?) = _uiState.examples.validateField(
            focusedTextFieldId = focusedTextFieldId,
            onAdd = _uiState::addExample
        )
    }

    private fun MutableMap<Long, String>.validateField(
        focusedTextFieldId: Long?,
        onAdd: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val latestId = maxOf { it.key } // latest id
            forEach { (id, tag) ->
                if (id != focusedTextFieldId && id != latestId && tag.isBlank()) {
                    remove(id)
                }
            }
            if (!this@validateField[latestId].isNullOrBlank()) {
                onAdd(INVALID_TEXT)
            }
        }
    }
}
