package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag

interface LanguageWordSpaceState {
    val isLoading: Boolean
    val isEditModeOn: Boolean
    val isEditDialogShown: Boolean

    val wordSpace: LanguageWordSpace
    val tags: List<MDEditableField<WordTypeTag>>

    fun toMutableLanguageWordSpaceWithTags(): LanguageWordSpaceMutableState = LanguageWordSpaceMutableState(this)
}
