package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

interface MDExportToFileBusinessUiActions {
}

interface MDExportToFileNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDExportToFileUiActions(
    navigationActions: MDExportToFileNavigationUiActions,
    businessActions: MDExportToFileBusinessUiActions,
) : MDExportToFileBusinessUiActions by businessActions, MDExportToFileNavigationUiActions by navigationActions