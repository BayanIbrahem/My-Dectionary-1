package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordWordClass

abstract class LanguageWordSpaceState(code: String, wordsCount: Int = 0): LanguageWordSpace(
    code = code,
    wordsCount = wordsCount
) {
    abstract val isLoading: Boolean
    abstract val isEditModeOn: Boolean
    abstract val isEditDialogShown: Boolean

    abstract val tags: List<MDEditableField<WordWordClass>>

    fun toMutableLanguageWordSpaceWithTags(): LanguageWordSpaceMutableState = LanguageWordSpaceMutableState(this)
}
