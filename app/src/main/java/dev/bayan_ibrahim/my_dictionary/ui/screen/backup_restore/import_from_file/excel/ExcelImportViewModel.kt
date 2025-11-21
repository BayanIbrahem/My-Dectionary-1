package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file.excel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.data.MDRoomImportFromExcelRepo
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.data_type_wrapper.excel.MDExcel
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDRowCell
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetDataType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetLanguageType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportDetails
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportProgress
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportProgressStatus
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetStatus
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeader
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderRole
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderTagRole
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderWordClassRole
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderWordRole
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.result.MDResult
import dev.bayan_ibrahim.my_dictionary.domain.model.result.asResult
import dev.bayan_ibrahim.my_dictionary.domain.model.result.fold
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcelImportViewModel @Inject constructor(
//    private val repo: ExcelImportRepo
    private val repo: MDRoomImportFromExcelRepo,
) : ViewModel() {
    private val _cancelImportDialogDismissSeconds = MutableStateFlow<Int?>(null)
    private val _uiState: ExcelImportMutableUiState = ExcelImportMutableUiState(_cancelImportDialogDismissSeconds)
    val uiState: ExcelImportUiState = _uiState
    fun initWithNavArgs(args: MDDestination) {

    }

    fun getUiActions(
        navActions: ExcelImportNavigationUiActions,
    ): ExcelImportUiActions = ExcelImportUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private var _excel: MDExcel? = null
    private fun detectExcel(): Flow<MDExcel> = repo.onBuildMDExcel(
        document = uiState.document,
        type = uiState.fileType,
    )

    suspend fun getOrDetectExcel(): Result<MDExcel> = _excel?.let {
        Result.success(it)
    } ?: detectExcel().asResult().firstOrNull()?.asResult()?.onSuccess {
        _excel = it
    } ?: Result.failure(IllegalArgumentException()) // TODO, error

    private fun getBusinessUiActions(
        navActions: ExcelImportNavigationUiActions,
    ): ExcelImportBusinessUiActions = object : ExcelImportBusinessUiActions {
        /**
         * pick a document try to get all sheets data and after getting all sheet data check if there
         * any broken sheets or if there is no valid sheet to mark the document as valid document or not
         */
        override fun onDocumentPick(document: MDDocumentData) {
            _uiState.document = document
            _uiState.sheets.clear()
            viewModelScope.launch {
                detectExcel().asResult()
                    .collect { result ->
                        result.fold(
                            onFailure = {
                                _uiState.validDocument = MDResult.failure(it)
                            },
                            onLoading = {
                                _uiState.validDocument = MDResult.loading()
                            },
                            onSuccess = { excel ->
                                _excel = excel
                                _uiState.validDocument = MDResult.loading()
                                val sheets = excel.getSheets().map { sheet ->
                                    var header = listOf<MDRowCell>()
                                    excel.readSheetRows(sheet, limit = 1) { _, cells ->
                                        header = cells
                                    }
                                    /**
                                     * this is the sheet headers according to its name
                                     * but it may be duplicated, so we make the non first value of each role an ignored value
                                     */

                                    val existsRoles = mutableSetOf<MDSheetHeaderRole>()
                                    val sheetHeaders = when (sheet.dataType) {
                                        MDSheetDataType.TAG -> {
                                            header.map { cell ->
                                                val name = cell.data.toStringValue()
                                                val role = MDSheetHeaderTagRole(name)
                                                if (role != null) {
                                                    MDSheetHeader.Assigned(
                                                        name = name,
                                                        index = cell.columnIndex,
                                                        suggestedRole = role,
                                                        explicitRote = role,
                                                    )
                                                } else {
                                                    MDSheetHeader.Ignored(
                                                        name = name,
                                                        index = cell.columnIndex,
                                                        suggestedRole = role,
                                                    )
                                                }
                                            }
                                        }

                                        MDSheetDataType.WORD -> {
                                            header.map { cell ->
                                                val name = cell.data.toStringValue()
                                                val role = MDSheetHeaderWordRole(name)
                                                if (role != null) {
                                                    MDSheetHeader.Assigned(
                                                        name = name,
                                                        index = cell.columnIndex,
                                                        suggestedRole = role,
                                                        explicitRote = role,
                                                    )
                                                } else {
                                                    MDSheetHeader.Ignored(
                                                        name = name,
                                                        index = cell.columnIndex,
                                                        suggestedRole = role,
                                                    )
                                                }
                                            }
                                        }

                                        MDSheetDataType.WORD_CLASS -> {
                                            header.map { cell ->
                                                val name = cell.data.toStringValue()
                                                val role = MDSheetHeaderWordClassRole(name)
                                                if (role != null) {
                                                    MDSheetHeader.Assigned(
                                                        name = name,
                                                        index = cell.columnIndex,
                                                        suggestedRole = role,
                                                        explicitRote = role,
                                                    )
                                                } else {
                                                    MDSheetHeader.Ignored(
                                                        name = name,
                                                        index = cell.columnIndex,
                                                        suggestedRole = role,
                                                    )
                                                }
                                            }
                                        }

                                        MDSheetDataType.GENERAL -> {
                                            header.map { cell ->
                                                MDSheetHeader.Ignored(
                                                    name = cell.data.toStringValue(),
                                                    index = cell.columnIndex,
                                                    suggestedRole = null,
                                                )
                                            }
                                        }
                                    }.toMutableList()

                                    sheetHeaders.indices.forEach { index ->
                                        val h = sheetHeaders[index]
                                        if (h is MDSheetHeader.Assigned) {
                                            if (h.explicitRote in existsRoles) {
                                                sheetHeaders[index] = MDSheetHeader.Ignored(
                                                    name = h.name,
                                                    index = h.index,
                                                    suggestedRole = h.suggestedRole
                                                )
                                            }
                                            existsRoles.add(h.explicitRote)
                                        }
                                    }

                                    val roles = when (sheet.dataType) {
                                        MDSheetDataType.TAG -> MDSheetHeaderTagRole.entries
                                        MDSheetDataType.WORD -> MDSheetHeaderWordRole.entries
                                        MDSheetDataType.WORD_CLASS -> MDSheetHeaderWordClassRole.entries
                                        MDSheetDataType.GENERAL -> emptyList()
                                    }.toSet()
                                    val status = when (sheet.dataType) {
                                        MDSheetDataType.TAG,
                                        MDSheetDataType.WORD,
                                        MDSheetDataType.WORD_CLASS,
                                            -> {
                                            val requiredRoles = roles.filter { it.required }
                                            val hasAllRequired = existsRoles.containsAll(requiredRoles)
                                            if (hasAllRequired) {
                                                MDSheetStatus.Assigned
                                            } else {
                                                MDSheetStatus.Broken
                                            }
                                        }

                                        MDSheetDataType.GENERAL -> {
                                            MDSheetStatus.Ignored
                                        }
                                    }
                                    MDSheetImportDetails(
                                        sheet = sheet,
                                        explicitLanguageType = null,
                                        explicitDataType = null,
                                        headers = sheetHeaders,
                                        roles = roles,
                                        status = status,
                                    )
                                }
                                _uiState.sheets.setAll(sheets)
                                val sheetsCounts = sheets.groupBy { it.status }.mapValues {
                                    it.value.count()
                                }
                                _uiState.validDocument =
                                    if (sheetsCounts.contains(MDSheetStatus.Broken) || !sheetsCounts.contains(MDSheetStatus.Assigned)) {
                                        MDResult.success(false)
                                    } else {
                                        MDResult.success(true)
                                    }
                            }
                        )
                    }
            }
        }

        /**
         * set [MDDocumentData.Blank] to current document
         * set [ExcelImportMutableUiState.validDocument] to false
         * clear sheet data.
         */

        override fun onDocumentDelete() {
            _uiState.document = MDDocumentData.Blank
            _uiState.validDocument = MDResult.success(false)
            _uiState.sheets.clear()
        }

        /**
         * change the document type and if the new type is different from the new type then we call
         * [onDocumentPick] again,
         */
        override fun onToggleFileType(type: MDExcelFileType) {
            val prevType = _uiState.fileType
            _uiState.explicitFileType = type
            if (prevType != _uiState.fileType) {
                onDocumentPick(_uiState.document)
            }
        }

        override fun onImport() {
            if (_uiState.validDocument.getOrNull() == true) {
                viewModelScope.launch {
                    val excel = getOrDetectExcel().getOrNull() ?: return@launch
                    val assignedSheets = _uiState.sheets.filter { it.status == MDSheetStatus.Assigned }
                    _uiState.importProgress.setAll(assignedSheets.map {
                        MDSheetImportProgress(
                            sheetName = it.name,
                            sheetDataType = it.dataType,
                            rows = it.actualLastRow - it.actualFirstRow,
                            importedRows = 0,
                            status = MDSheetImportProgressStatus.Queue
                        )
                    })
                    repo.onImport(
                        excel = excel,
                        sheets = assignedSheets,
                        corruptionStrategy = uiState.corruptedWordStrategy,
                        conflictStrategy = uiState.existedWordStrategy,
                        onAction = { sheetIndex, sheet, rowIndex, status ->
                            _uiState.importProgress[sheetIndex] = MDSheetImportProgress(
                                sheetName = sheet.name,
                                sheetDataType = sheet.dataType,
                                rows = sheet.actualLastRow - sheet.actualFirstRow,
                                importedRows = rowIndex - sheet.actualFirstRow,
                                status = status
                            )
                        }
                    )
                }
            }
        }

        override fun onCorruptedWordStrategyChange(strategy: MDPropertyCorruptionStrategy) {
            TODO("Not yet implemented")
        }

        override fun onConflictedWordStrategyChange(strategy: MDPropertyConflictStrategy) {
            TODO("Not yet implemented")
        }

        override fun onExtraTagsStrategyChange(strategy: MDExtraTagsStrategy) {
            TODO("Not yet implemented")
        }

        override fun onResetSheet(sheet: MDSheetImportDetails) {
            TODO("Not yet implemented")
        }

        override fun onIgnoreSheet(sheet: MDSheetImportDetails) {
            TODO("Not yet implemented")
        }

        override fun onAssignSheet(sheet: MDSheetImportDetails) {
            TODO("Not yet implemented")
        }

        override fun onAutoAssignSheet(sheet: MDSheetImportDetails) {
            TODO("Not yet implemented")
        }

        override fun onAssignSheetLanguage(sheet: MDSheetImportDetails, language: MDSheetLanguageType) {
            TODO("Not yet implemented")
        }

        override fun onResetDefaultSheetLanguage(sheet: MDSheetImportDetails) {
            TODO("Not yet implemented")
        }

        override fun onSetFirstImportRow(row: Int) {
            TODO("Not yet implemented")
        }

        override fun onSetLastImportRow(row: Int) {
            TODO("Not yet implemented")
        }

        override fun onSetImportRowsLimit(limit: Int) {
            TODO("Not yet implemented")
        }

        override fun onAssignHeaderRole(sheet: MDSheetImportDetails, header: MDSheetHeader, role: MDSheetHeaderRole) {
            TODO("Not yet implemented")
        }

        override fun onAutoAssignHeaderRole(sheet: MDSheetImportDetails, header: MDSheetHeader) {
            TODO("Not yet implemented")
        }

        override fun onResetHeaderRole(sheet: MDSheetImportDetails, header: MDSheetHeader) {
            TODO("Not yet implemented")
        }

        override fun onCancelImport() {
            TODO("Not yet implemented")
        }
    }

}
