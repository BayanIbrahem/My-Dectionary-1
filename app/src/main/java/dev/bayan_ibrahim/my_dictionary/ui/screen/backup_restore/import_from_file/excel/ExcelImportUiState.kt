package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file.excel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportDetails
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportProgress
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.result.MDResult
import kotlinx.coroutines.flow.Flow


interface ExcelImportUiState : MDUiState {
    val document: MDDocumentData

    /**
     * detected file type if no [explicitFileType] exists
     */
    val detectedFileType: MDExcelFileType?

    /**
     * explicit file type
     */
    val explicitFileType: MDExcelFileType?

    /**
     * check for [explicitFileType] then [detectedFileType]
     */
    val fileType: MDExcelFileType? get() = explicitFileType ?: detectedFileType

    /**
     * result if current document if can be parsed using the current [detectedFileType]
     */
    val validDocument: MDResult<Boolean>

    /**
     * if true then the current file is ready to import
     */
    val validToImport: MDResult<Boolean>
    val existedWordStrategy: MDPropertyConflictStrategy
    val corruptedWordStrategy: MDPropertyCorruptionStrategy
    val extraTagsStrategy: MDExtraTagsStrategy
    val sheets: List<MDSheetImportDetails>
    val importProgress: List<MDSheetImportProgress>

    /**
     * this flow contains the seconds count for cancel confirm auto dismiss
     * * if the value is `null`: then import cancel require confirm
     * * if the value is not `null` then the value of the flow is the seconds before auto dismiss
     * and emit the `null` value again, while the value is not null then cancel import doesn't require
     * confirm
     */
    val cancelImportConfirmDuration: Flow<Int?>
}

class ExcelImportMutableUiState(
    override val cancelImportConfirmDuration: Flow<Int?>,
) : ExcelImportUiState, MDMutableUiState() {
    override var document: MDDocumentData by mutableStateOf(MDDocumentData.Blank)
    override var detectedFileType: MDExcelFileType? by mutableStateOf(null)
    override var explicitFileType: MDExcelFileType? by mutableStateOf(null)
    override var validDocument: MDResult<Boolean> by mutableStateOf(MDResult.loading())
    override var validToImport: MDResult<Boolean> by mutableStateOf(MDResult.loading())
    override var existedWordStrategy: MDPropertyConflictStrategy by mutableStateOf(MDPropertyConflictStrategy.IgnoreProperty)
    override var corruptedWordStrategy: MDPropertyCorruptionStrategy by mutableStateOf(MDPropertyCorruptionStrategy.IgnoreProperty)
    override var extraTagsStrategy: MDExtraTagsStrategy by mutableStateOf(MDExtraTagsStrategy.All)
    override val sheets: SnapshotStateList<MDSheetImportDetails> = mutableStateListOf()
    override var importProgress: SnapshotStateList<MDSheetImportProgress> = mutableStateListOf()
}