package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.WordDetailsDirectionSource
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDProfileBusinessUiActions {
    fun onToggleLiveTemplate(liveTemplate: Boolean)
    fun onToggleWordDetailsAlignmentSource(source: WordDetailsDirectionSource)
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
