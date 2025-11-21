package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file.excel

import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetLanguageType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportDetails
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeader
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderRole
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface ExcelImportBusinessUiActions {
    fun onDocumentPick(document: MDDocumentData)
    fun onDocumentDelete()
    fun onToggleFileType(type: MDExcelFileType)
    fun onImport()
    fun onCorruptedWordStrategyChange(strategy: MDPropertyCorruptionStrategy)
    fun onConflictedWordStrategyChange(strategy: MDPropertyConflictStrategy)
    fun onExtraTagsStrategyChange(strategy: MDExtraTagsStrategy)

    /**
     * only for sheets that has changes, used also in header management screen
     */
    fun onResetSheet(sheet: MDSheetImportDetails)

    /**
     * only for sheets that is not ignored
     */
    fun onIgnoreSheet(sheet: MDSheetImportDetails)

    /**
     * for any sheet
     */
    fun onAssignSheet(sheet: MDSheetImportDetails)

    /**
     * only for sheets that has a suggested data type
     */
    fun onAutoAssignSheet(sheet: MDSheetImportDetails)

    fun onAssignSheetLanguage(sheet: MDSheetImportDetails, language: MDSheetLanguageType)
    fun onResetDefaultSheetLanguage(sheet: MDSheetImportDetails)
    fun onSetFirstImportRow(row: Int)
    fun onSetLastImportRow(row: Int)
    fun onSetImportRowsLimit(limit: Int)
    fun onAssignHeaderRole(sheet: MDSheetImportDetails, header: MDSheetHeader, role: MDSheetHeaderRole)

    /**
     * works only for headers that have suggested role
     */
    fun onAutoAssignHeaderRole(sheet: MDSheetImportDetails, header: MDSheetHeader)

    /**
     * works only for headers that doesn't have an explicit role
     */
    fun onResetHeaderRole(sheet: MDSheetImportDetails, header: MDSheetHeader)

    /**
     * same function for cancel import first click and the confirm click
     */
    fun onCancelImport()
}

interface ExcelImportNavigationUiActions : MDAppNavigationUiActions {
    fun onNavigateToHowImportWorks()

    /**
     * navigate to sheets management screen
     */
    fun onNavigateToSheetManagement()

    /**
     * navigate to header managements screen
     */
    fun onNavigateToHeaderManagement(sheet: MDSheetImportDetails)

    /*

    navigate to import progress screen
     */
    fun onNavigateToImportProgress()
}

@androidx.compose.runtime.Immutable
class ExcelImportUiActions(
    navigationActions: ExcelImportNavigationUiActions,
    businessActions: ExcelImportBusinessUiActions,
) : ExcelImportBusinessUiActions by businessActions, ExcelImportNavigationUiActions by navigationActions