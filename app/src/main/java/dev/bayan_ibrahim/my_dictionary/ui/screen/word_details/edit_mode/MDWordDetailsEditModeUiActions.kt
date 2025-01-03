package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode

import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDWordDetailsEditModeBusinessUiActions {
    fun onSave()
    fun onEditMeaning(newMeaning: String)
    fun onEditTranslation(newTranslation: String)
    fun onEditTranscription(newTranscription: String)
    fun onEditAdditionalTranslations(id: Long, newValue: String)
    fun onEditExamples(id: Long, newValue: String)
    fun onEditTypeTag(newTypeTag: WordTypeTag?)
    fun onEditTypeRelationLabel(id: Long, relation: WordTypeTagRelation)
    fun onEditTypeRelationValue(id: Long, newValue: String)
    fun onEditLexicalRelation(type: WordLexicalRelationType, id: Long, newValue: String)

    fun onAdditionalTranslationsFocusChange(newFocused: Long)
    fun onExamplesFocusChange(newFocused: Long)
    fun onTypeRelationFocusChange(newFocused: Long)
    fun onLexicalRelationFocusChange(newFocused: Long)
    fun onFocusChange(newFocused: Long)
}

interface MDWordDetailsEditModeNavigationUiActions : MDAppNavigationUiActions {
    fun onNavigateToViewMode(newWordId: Long, language: LanguageCode)
}

@androidx.compose.runtime.Immutable
class MDWordDetailsEditModeUiActions(
    navigationActions: MDWordDetailsEditModeNavigationUiActions,
    businessActions: MDWordDetailsEditModeBusinessUiActions,
) : MDWordDetailsEditModeBusinessUiActions by businessActions, MDWordDetailsEditModeNavigationUiActions by navigationActions