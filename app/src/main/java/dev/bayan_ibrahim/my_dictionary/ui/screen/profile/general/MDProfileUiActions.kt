package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDProfileBusinessUiActions {
    fun onToggleLiveTemplate(liveTemplate: Boolean)
}

interface MDProfileNavigationUiActions: MDAppNavigationUiActions {
    fun navigateToImportFromFile()
    fun navigateToExportToFile()
    fun navigateToSync()
    fun navigateToAppTheme()
}

@Immutable
class MDProfileUiActions(
    navigationActions: MDProfileNavigationUiActions,
    businessActions: MDProfileBusinessUiActions,
) : MDProfileBusinessUiActions by businessActions, MDProfileNavigationUiActions by navigationActions
