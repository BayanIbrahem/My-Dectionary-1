package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagsTree

interface MDTagsSelectorUiState {
    val searchQuery: String
    val disabledTags: Set<Tag>
    val allTagsTree: TagsTree
    val currentTagsTree: TagsTree
    val selectedTags: List<Tag>
    val isSelectEnabled: Boolean
}