package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDTagsSelectorBusinessUiActions {
    /**
     * on toggle expand tag
     */
    fun onClickTag(tag: Tag, index: Int)

    /**
     * remove any sub tags selected and add this tag
     */
    fun onToggleSelectTag(tag: Tag, select: Boolean)
    fun onAddChild(parent: Tag?)
    fun onRequestEditTagLabel(tag: Tag)
    fun onConfirmEditTagLabel(tag: Tag, newLabel: String)
    fun onCancelEditTagLabel()
    fun onDeleteTag(tag: Tag)
    fun onDeleteTagSubtree(tag: Tag)
    fun onSetAllowedTagsFilter(filter: (Tag) -> Boolean)
    fun onResetAllowedTagsFilter()
    fun onSetForbiddenTagsFilter(filter: (Tag) -> Boolean)
    fun onResetForbiddenTagsFilter()
    fun onSearchQueryChange(query: String)
    fun onClearSearchQuery()
    fun onToggleSelectEnabled(selectEnabled: Boolean)
    fun onToggleAddEnabled(addEnabled: Boolean)
    fun onToggleEditEnabled(editEnabled: Boolean)
    fun onToggleDeleteEnabled(deleteEnabled: Boolean)
    fun onToggleDeleteSubtreeEnabled(deleteSubtreeEnabled: Boolean)
    fun onChangeParent(tag: Tag, parent: Tag? = null)
    fun onConfirmSelectedTags()
    fun onEditColor(id: Long, color: Color?, bool: Boolean)
}

interface MDTagsSelectorNavigationUiActions: MDAppNavigationUiActions {
    suspend fun onAnimateScrollToIndex(index: Int) {

    }

    fun onNotifyConfirmSelectedTags(selectedTags: List<Tag>) {

    }
}

@androidx.compose.runtime.Immutable
class MDTagsSelectorUiActions(
    navigationActions: MDTagsSelectorNavigationUiActions,
    businessActions: MDTagsSelectorBusinessUiActions,
) : MDTagsSelectorBusinessUiActions by businessActions, MDTagsSelectorNavigationUiActions by navigationActions {
}