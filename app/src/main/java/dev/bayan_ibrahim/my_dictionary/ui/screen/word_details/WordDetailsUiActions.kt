package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface WordDetailsBusinessUiActions {
    fun onEnableEditMode()
    fun onCancelChanges()
    fun onSaveChanges()
    fun onMeaningChange(newMeaning: String)
    fun onTranslationChange(newTranslation: String)
    fun onTranscriptionChange(newTranscription: String)
    fun onEditAdditionalTranslation(id: Long, newAdditionalTranslation: String)
    fun onValidateAdditionalTranslations(focusedTextFieldId: Long? = null)
    fun onChangeTypeTag(newTypeTag: WordTypeTag?)
    fun onAddNewRelatedWord(relation: WordTypeTagRelation)
    fun onEditRelatedWordRelation(id: Long, newRelation: WordTypeTagRelation)
    fun onEditRelatedWordValue(id: Long, newValue: String)
    fun onRemoveRelatedWord(id: Long, relation: String, value: String)
    fun onValidateRelatedWords(focusedTextFieldId: Long? = null)
    fun onEditExample(id: Long, newExample: String)
    fun onValidateExamples(focusedTextFieldId: Long? = null)
}

interface WordDetailsNavigationUiActions: MDAppNavigationUiActions {
    fun pop()
    fun navigateToWordStatistics(wordId: Long)
}

@Immutable
class WordDetailsUiActions(
    navigationActions: WordDetailsNavigationUiActions,
    businessActions: WordDetailsBusinessUiActions,
) : WordDetailsBusinessUiActions by businessActions, WordDetailsNavigationUiActions by navigationActions {
}