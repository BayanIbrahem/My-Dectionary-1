package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileType

interface MDImportFromFileBusinessUiActions {
    fun onSelectFile(fileData: MDFileData)
    fun onSelectFileType(selectedFileType: MDFileType?)
    fun onOverrideFileTypeCheckChange(checked: Boolean)

    fun onChangeCorruptedWordStrategy(strategy: MDFileStrategy)
    fun onChangeExistedWordStrategy(strategy: MDFileStrategy)
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