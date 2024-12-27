package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags

import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDMigrateTagsBusinessUiActions {
}

interface MDMigrateTagsNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDMigrateTagsUiActions(
    navigationActions: MDMigrateTagsNavigationUiActions,
    businessActions: MDMigrateTagsBusinessUiActions,
) : MDMigrateTagsBusinessUiActions by businessActions, MDMigrateTagsNavigationUiActions by navigationActions