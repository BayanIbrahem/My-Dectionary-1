package dev.bayan_ibrahim.my_dictionary.ui.screen.profile

import androidx.compose.runtime.Immutable

interface MDProfileBusinessUiActions {
}

interface MDProfileNavigationUiActions {
    fun navigateToImportFromFile()
    fun navigateToExportToFile()
    fun navigateToAutoBackup()
}

@Immutable
class MDProfileUiActions(
    navigationActions: MDProfileNavigationUiActions,
    businessActions: MDProfileBusinessUiActions,
) : MDProfileBusinessUiActions by businessActions, MDProfileNavigationUiActions by navigationActions