package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordDetailsRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    private val repo: WordDetailsRepo,
) : ViewModel() {
    private val state: WordDetailsMutableUiState = WordDetailsMutableUiState()
    fun initWithNavigationArgs(args: MDDestination.WordDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            args.wordId?.let { id ->
                val word = repo.getWord(id)
                state.loadWord(word)
            }
            val tags = repo.getLanguageTags(args.languageCode)
            state.typeTags.setAll(tags)
            // if it is a new word then it should be the on edit mode initially otherwise it should be on reading mode
            state.isEditModeOn = args.wordId == null
        }
    }

    private fun ensureEditableUiState(scope: WordDetailsMutableUiState.() -> Unit) {
        if (!state.isLoading && state.isEditModeOn) {
            state.scope()
        }
    }

    fun getUiActions(
        navigationActions: WordDetailsNavigationUiActions,
    ): WordDetailsBusinessUiActions = object : WordDetailsBusinessUiActions {
        override fun onEnableEditMode() {
            if (!state.isLoading) {
                state.isEditModeOn = true
            }
        }

        override fun onSaveChanges() = ensureEditableUiState {
            viewModelScope.launch(Dispatchers.IO) {
                state.onExecute {
                    state.isEditModeOn = false
                    onValidateAdditionalTranslations()
                    onValidateTags()
                    onValidateRelatedWords()
                    onValidateExamples()
                    val word = state.toWord()
                    if (word.id == INVALID_ID) {
                        val newWord = repo.saveNewWord(word)
                        // we can import the whole word but this is faster
                        state.id = newWord.id
                    } else {
                        repo.saveExistedWord(word)
                    }
                    true
                }
            }
        }

        override fun onCancelChanges() = ensureEditableUiState {
            if (state.id == INVALID_ID) {
                navigationActions.pop()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    state.onExecute {
                        val navArgs = MDDestination.WordDetails(
                            wordId = state.id,
                            languageCode = state.language.code
                        )
                        initWithNavigationArgs(navArgs)
                        state.isEditModeOn = false
                        true
                    }
                }
            }
        }

        override fun onMeaningChanged(newMeaning: String) = ensureEditableUiState {
            meaning = newMeaning
        }

        override fun onTranscriptionChanged(newTranscription: String) = ensureEditableUiState {
            transcription = newTranscription
        }

        override fun onTranslationChanged(newTranslation: String) = ensureEditableUiState {
            translation = newTranslation
        }

        override fun onAddNewAdditionalTranslation() = ensureEditableUiState {
            addAdditionalTranslation(INVALID_TEXT)
        }

        override fun onEditAdditionalTranslation(id: Long, newAdditionalTranslation: String) = ensureEditableUiState {
            additionalTranslations[id] = newAdditionalTranslation
        }

        override fun onRemoveAdditionalTranslation(id: Long, additionalTranslation: String) = ensureEditableUiState {
            additionalTranslations.remove(id)
        }

        override fun onValidateAdditionalTranslations(focusedTextFieldId: Long?) {
            state.additionalTranslations.forEach { (id, translation) ->
                if (id != focusedTextFieldId && translation.isBlank()) {
                    state.additionalTranslations.remove(id)
                }
            }
        }

        override fun onAddNewTag() = ensureEditableUiState {
            addTag(INVALID_TEXT)
        }

        override fun onEditTag(id: Long, newTag: String) = ensureEditableUiState {
            tags[id] = newTag
        }

        override fun onRemoveTag(id: Long, tag: String) = ensureEditableUiState {
            tags.remove(id)
        }

        override fun onValidateTags(focusedTextFieldId: Long?) = ensureEditableUiState {
            tags.forEach { (id, tag) ->
                if (id != focusedTextFieldId && tag.isBlank()) {
                    tags.remove(id)
                }
            }
        }

        override fun onChangeTypeTag(newTypeTag: WordTypeTag?) = ensureEditableUiState {
            this.selectedTypeTag = newTypeTag
        }

        override fun onAddNewRelatedWord(relation: String) = ensureEditableUiState {
            addRelatedWord(relation, INVALID_TEXT)
        }

        override fun onEditRelatedWordRelation(id: Long, newRelation: String) = ensureEditableUiState {
            val oldValue = relatedWords[id]?.second ?: INVALID_TEXT
            relatedWords[id] = newRelation to oldValue
        }

        override fun onEditRelatedWordValue(id: Long, newValue: String) = ensureEditableUiState {
            val oldRelation = relatedWords[id]?.second ?: return@ensureEditableUiState
            relatedWords[id] = oldRelation to newValue
        }

        override fun onRemoveRelatedWord(id: Long, relation: String, value: String) {
            state.relatedWords.remove(id)
        }

        override fun onValidateRelatedWords(focusedTextFieldId: Long?) {
            state.relatedWords.forEach { (id, relatedWord) ->
                if (id != focusedTextFieldId && relatedWord.second.isBlank()) {
                    state.relatedWords.remove(id)
                }
            }
        }

        override fun onAddNewExample() = ensureEditableUiState {
            addExample(INVALID_TEXT)
        }

        override fun onEditExample(id: Long, newExample: String) = ensureEditableUiState {
            examples[id] = newExample
        }

        override fun onRemoveExample(id: Long, example: String) = ensureEditableUiState {
            examples.remove(id)
        }

        override fun onValidateExamples(focusedTextFieldId: Long?) {
            state.examples.forEach { (id, example) ->
                if (id != focusedTextFieldId && example.isBlank()) {
                    state.examples.remove(id)
                }
            }
        }
    }
}