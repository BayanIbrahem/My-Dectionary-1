package dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag

import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag


interface MDContextTagsSelectorBusinessUiActions {
    fun onClickTag(tag: ContextTag)
    /**
     * remove any sub tags selected and add this tag
     */
    fun onSelectTag(tag: ContextTag)
    fun onSelectCurrentTag()
    fun onUnSelectTag(tag: ContextTag)
    fun onSetInitialSelectedTags(tags: Collection<ContextTag>)
    fun clearSelectedTags()
    fun onAddNewContextTag(tag: ContextTag)
    fun onAddNewContextTag(segment: String)
    fun onDeleteContextTag(tag: ContextTag)
    fun onNavigateUp()
    fun onResetToRoot()
    fun onSetAllowedTagsFilter(filter: (ContextTag) -> Boolean)
    fun onResetAllowedTagsFilter()
    fun onSetForbiddenTagsFilter(filter: (ContextTag) -> Boolean)
    fun onResetForbiddenTagsFilter()
    fun onResetTagsFilter()
    fun onSearchQueryChange(query: String)
    fun refreshCurrentTree()
}

interface MDContextTagsSelectorNavigationUiActions {
    fun onUpdateSelectedTags(selectedTags: SnapshotStateList<ContextTag>) {

    }
}

@androidx.compose.runtime.Immutable
class MDContextTagsSelectorUiActions(
    navigationActions: MDContextTagsSelectorNavigationUiActions,
    businessActions: MDContextTagsSelectorBusinessUiActions,
) : MDContextTagsSelectorBusinessUiActions by businessActions, MDContextTagsSelectorNavigationUiActions by navigationActions