package dev.bayan_ibrahim.my_dictionary.core.ui.context_tag

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsTree

interface MDContextTagsSelectionActions {
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