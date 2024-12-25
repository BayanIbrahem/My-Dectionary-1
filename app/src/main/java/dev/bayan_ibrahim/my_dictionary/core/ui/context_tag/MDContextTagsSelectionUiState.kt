package dev.bayan_ibrahim.my_dictionary.core.ui.context_tag

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsTree

interface MDContextTagsSelectionUiState {
    val searchQuery: String
    val disabledTags: Set<ContextTag>
    val allTagsTree: ContextTagsTree
    val currentTagsTree: ContextTagsTree
    val selectedTags: List<ContextTag>
}