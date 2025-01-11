package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import android.net.Uri
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDExportToFileBusinessUiActions {
    fun onToggleSelectAvailablePart(type: MDFilePartType, selected: Boolean)
    fun onStartExportProcess()
    fun onSelectExportFileType(type: MDFileType)
    fun onExportFileNameChange(newName: String)
    fun onExportDirectoryChange(uri: Uri)
    fun onCancelExport()
}

interface MDExportToFileNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDExportToFileUiActions(
    navigationActions: MDExportToFileNavigationUiActions,
    businessActions: MDExportToFileBusinessUiActions,
) : MDExportToFileBusinessUiActions by businessActions, MDExportToFileNavigationUiActions by navigationActions