package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag


interface MDTagsSelectorBusinessUiActions {
    fun onClickTag(tag: Tag)
    /**
     * remove any sub tags selected and add this tag
     */
    fun onSelectTag(tag: Tag)
    fun onSelectCurrentTag()
    fun onUnSelectTag(tag: Tag)
    fun onSetInitialSelectedTags(tags: Collection<Tag>)
    fun clearSelectedTags()
    fun onAddNewTag(tag: Tag)
    fun onAddNewTag(segment: String)
    fun onDeleteTag(tag: Tag)
    fun onNavigateUp()
    fun onResetToRoot()
    fun onSetAllowedTagsFilter(filter: (Tag) -> Boolean)
    fun onResetAllowedTagsFilter()
    fun onSetForbiddenTagsFilter(filter: (Tag) -> Boolean)
    fun onResetForbiddenTagsFilter()
    fun onResetTagsFilter()
    fun onSearchQueryChange(query: String)
    fun refreshCurrentTree()
}

interface MDTagsSelectorNavigationUiActions {
    fun onUpdateSelectedTags(selectedTags: SnapshotStateList<Tag>) {

    }
}

@androidx.compose.runtime.Immutable
class MDTagsSelectorUiActions(
    navigationActions: MDTagsSelectorNavigationUiActions,
    businessActions: MDTagsSelectorBusinessUiActions,
) : MDTagsSelectorBusinessUiActions by businessActions, MDTagsSelectorNavigationUiActions by navigationActions