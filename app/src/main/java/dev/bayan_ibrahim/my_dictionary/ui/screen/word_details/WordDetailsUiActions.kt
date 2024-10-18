package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.WordTypeTag


interface WordDetailsBusinessUiActions {
    fun onEnableEditMode()
    fun onCancelChanges()
    fun onSaveChanges()
    fun onMeaningChanged(newMeaning: String)
    fun onTranslationChanged(newTranslation: String)
    fun onTranscriptionChanged(newTranscription: String)
    fun onAddNewAdditionalTranslation()
    fun onEditAdditionalTranslation(id: Long, newAdditionalTranslation: String)
    fun onRemoveAdditionalTranslation(id: Long, additionalTranslation: String)
    fun onValidateAdditionalTranslations(focusedTextFieldId: Long? = null)
    fun onAddNewTag()
    fun onEditTag(id: Long, newTag: String)
    fun onRemoveTag(id: Long, tag: String)
    fun onValidateTags(focusedTextFieldId: Long? = null)
    fun onChangeTypeTag(newTypeTag: WordTypeTag?)
    fun onAddNewRelatedWord(relation: String)
    fun onEditRelatedWordRelation(id: Long, newRelation: String)
    fun onEditRelatedWordValue(id: Long, newValue: String)
    fun onRemoveRelatedWord(id: Long, relation: String, value: String)
    fun onValidateRelatedWords(focusedTextFieldId: Long? = null)
    fun onAddNewExample()
    fun onEditExample(id: Long, newExample: String)
    fun onRemoveExample(id: Long, example: String)
    fun onValidateExamples(focusedTextFieldId: Long? = null)
}

interface WordDetailsNavigationUiActions {
    fun pop()
}

interface WordDetailsUiActions : WordDetailsBusinessUiActions, WordDetailsNavigationUiActions
