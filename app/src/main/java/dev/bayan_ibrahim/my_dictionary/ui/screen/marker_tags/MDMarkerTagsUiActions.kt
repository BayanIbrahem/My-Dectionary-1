package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags

import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDMarkerTagsBusinessUiActions {
}

interface MDMarkerTagsNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDMarkerTagsUiActions(
    navigationActions: MDMarkerTagsNavigationUiActions,
    businessActions: MDMarkerTagsBusinessUiActions,
) : MDMarkerTagsBusinessUiActions by businessActions, MDMarkerTagsNavigationUiActions by navigationActions