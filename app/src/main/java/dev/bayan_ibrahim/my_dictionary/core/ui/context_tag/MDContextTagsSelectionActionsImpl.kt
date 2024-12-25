package dev.bayan_ibrahim.my_dictionary.core.ui.context_tag

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsMutableTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.parentOrNull
import kotlinx.collections.immutable.toPersistentSet

class MDContextTagsSelectionActionsImpl(
    private val state: MDContextTagsSelectionMutableUiState,
    private val onAddNewTag: (tag: ContextTag) -> Unit,
    private val onDeleteTag: (tag: ContextTag) -> Unit,
    private val onUpdateSelectedTags: (List<ContextTag>) -> Unit = {},
) : MDContextTagsSelectionActions {
    private var allowedFilter: (ContextTag) -> Boolean = { true }
    private var forbiddenFilter: (ContextTag) -> Boolean = { false }
    private fun setCurrentTree(tree: ContextTagsTree) {
        state.currentTagsTree.setFrom(tree)
        onUpdateFilteredTags()
    }

    private fun onUpdateFilteredTags() {
        val subTags = state.currentTagsTree.nextLevel.values.mapNotNull { it.tag }
        val removedTags = subTags.filter {
            !allowedFilter(it) || forbiddenFilter(it) || it in state.selectedTags
        }
        removedTags.forEach {
            state.currentTagsTree.removeTag(it)
        }
    }

    private fun onUpdateDisabledTags() {
        val query = state.searchQuery
        state.disabledTags = state.currentTagsTree.nextLevel.mapNotNull {
            val matchQuery = it.key.tagMatchNormalize.startsWith(query.tagMatchNormalize)
            val isSelected = state.selectedTags.contains(it.value.tag)
            if (!matchQuery || isSelected) {
                it.value.tag
            } else null
        }.toPersistentSet()
    }

    private fun onUpdateSelectedTags() {
        onUpdateSelectedTags(state.selectedTags)
        onUpdateFilteredTags()
        onUpdateDisabledTags()
    }

    override fun onClickTag(tag: ContextTag) {
        setCurrentTree(state.allTagsTree[tag] ?: state.allTagsTree)
        onSearchQueryChange(state.searchQuery)
    }

    override fun onSearchQueryChange(query: String) {
        state.searchQuery = query
        onUpdateDisabledTags()
    }


    override fun onSelectTag(tag: ContextTag) {
        if (state.selectedTags.any { it.contains(tag) }) {
            // if a parent of this tag already exists then we don't add this tag
            return
        }
        state.selectedTags.removeIf {
            it.isContained(tag) // current param tag return true in isContained so we make adding after removing
        }
        state.selectedTags.add(tag)
        onUpdateSelectedTags()
        onResetToRoot()
    }

    override fun onSelectCurrentTag() {
        state.currentTagsTree.tag?.let { onSelectTag(it) }
    }

    override fun onUnSelectTag(tag: ContextTag) {
        state.selectedTags.remove(tag)
        onUpdateSelectedTags()
    }

    override fun onSetInitialSelectedTags(tags: Collection<ContextTag>) {
        state.selectedTags.setAll(tags)
        onUpdateSelectedTags()
    }

    override fun clearSelectedTags() {
        state.selectedTags.clear()
        onUpdateSelectedTags()
    }

    override fun onAddNewContextTag(tag: ContextTag) {
        onAddNewTag(tag)
        val mutableCurrent = ContextTagsMutableTree(state.currentTagsTree)
        mutableCurrent.addTag(tag)
        setCurrentTree(mutableCurrent)
    }

    override fun onAddNewContextTag(segment: String) {
        if (segment.isNotBlank()) {
            val tag = ContextTag(state.currentTagsTree.tag, segment)
            onAddNewContextTag(tag)
        }
    }

    override fun onDeleteContextTag(tag: ContextTag) {
        onDeleteTag(tag)
        val mutableCurrent = ContextTagsMutableTree(state.currentTagsTree)
        mutableCurrent.removeTag(tag)
        setCurrentTree(mutableCurrent)
    }

    override fun onNavigateUp() {
        state.currentTagsTree.tag?.parentOrNull?.let(::onClickTag) ?: setCurrentTree(state.allTagsTree)
    }

    override fun onResetToRoot() {
        setCurrentTree(state.allTagsTree)
    }

    override fun onSetAllowedTagsFilter(filter: (ContextTag) -> Boolean) {
        allowedFilter = filter
        onUpdateFilteredTags()
    }

    override fun onResetAllowedTagsFilter() {
        allowedFilter = { true }
        onUpdateFilteredTags()
    }

    override fun onSetForbiddenTagsFilter(filter: (ContextTag) -> Boolean) {
        forbiddenFilter = filter
        onUpdateFilteredTags()
    }

    override fun onResetForbiddenTagsFilter() {
        forbiddenFilter = { false }
        onUpdateFilteredTags()
    }

    override fun onResetTagsFilter() {
        allowedFilter = { true }
        forbiddenFilter = { false }
        onUpdateFilteredTags()
    }

    override fun refreshCurrentTree() {
        setCurrentTree(
            state.currentTagsTree.tag?.let { tag ->
                state.allTagsTree[tag]
            } ?: state.allTagsTree
        )
    }
}