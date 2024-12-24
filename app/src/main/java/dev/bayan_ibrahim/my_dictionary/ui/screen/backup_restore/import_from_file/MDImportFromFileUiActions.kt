package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy

interface MDImportFromFileBusinessUiActions {
    fun onSelectFile(fileData: MDFileData)
    fun onSelectFileType(selectedFileType: MDFileType?)
    fun onOverrideFileTypeCheckChange(checked: Boolean)

    fun onChangeCorruptedWordStrategy(strategy: MDPropertyCorruptionStrategy)
    fun onChangeExistedWordStrategy(strategy: MDPropertyConflictStrategy)
    fun onStartImportProcess()
    fun onCancelImportProcess()
}

interface MDImportFromFileNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDImportFromFileUiActions(
    navigationActions: MDImportFromFileNavigationUiActions,
    businessActions: MDImportFromFileBusinessUiActions,
) : MDImportFromFileBusinessUiActions by businessActions, MDImportFromFileNavigationUiActions by navigationActions