package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags

import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDMarkerTagsBusinessUiActions {
    fun updateTag(tag: Tag)
    fun removeTag(tag: Tag)
    fun onUpdateSelectedTags(selectedTags: List<Tag>)
}

interface MDMarkerTagsNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDMarkerTagsUiActions(
    navigationActions: MDMarkerTagsNavigationUiActions,
    businessActions: MDMarkerTagsBusinessUiActions,
) : MDMarkerTagsBusinessUiActions by businessActions, MDMarkerTagsNavigationUiActions by navigationActions