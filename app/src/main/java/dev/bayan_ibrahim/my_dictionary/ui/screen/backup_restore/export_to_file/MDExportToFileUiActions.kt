package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDExportToFileBusinessUiActions {
}

interface MDExportToFileNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDExportToFileUiActions(
    navigationActions: MDExportToFileNavigationUiActions,
    businessActions: MDExportToFileBusinessUiActions,
) : MDExportToFileBusinessUiActions by businessActions, MDExportToFileNavigationUiActions by navigationActions