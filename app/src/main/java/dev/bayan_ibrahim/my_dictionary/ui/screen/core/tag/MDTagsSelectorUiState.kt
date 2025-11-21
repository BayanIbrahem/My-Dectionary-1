package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagsTree

interface MDTagsSelectorUiState {
    val searchQuery: String
    val disabledTags: Set<Long>
    val tagsTree: TagsTree
    // this is the visible tags list in order, (non expanded tags content is not visible,
    // and this list is used to calculate the vertical db distance between a child and its parent
    // using the index of each one in the list
    val visibleTagsList: List<Tag>

    val selectedTagsIds: Set<Long>
    val isSelectEnabled: Boolean
    val isAddEnabled: Boolean
    val isEditEnabled: Boolean
    val isDeleteEnabled: Boolean
    val isDeleteSubtreeEnabled: Boolean
    val currentEditTagId: Long?

    companion object {
        const val NEW_TAG_ID: Long = -10L
    }
}