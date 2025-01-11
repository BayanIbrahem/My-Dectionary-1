package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags

import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDMarkerTagsBusinessUiActions {
    fun updateTag(tag: ContextTag)
    fun removeTag(tag: ContextTag)
    fun onUpdateSelectedTags(selectedTags: List<ContextTag>)
}

interface MDMarkerTagsNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDMarkerTagsUiActions(
    navigationActions: MDMarkerTagsNavigationUiActions,
    businessActions: MDMarkerTagsBusinessUiActions,
) : MDMarkerTagsBusinessUiActions by businessActions, MDMarkerTagsNavigationUiActions by navigationActions