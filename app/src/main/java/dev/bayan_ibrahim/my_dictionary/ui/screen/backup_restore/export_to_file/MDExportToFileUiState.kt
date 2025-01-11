package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data.ExportProgress
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.util.MDExportToFilePreferences

interface MDExportToFileUiState : MDUiState {
    val exportPreferences: MDExportToFilePreferences
    val selectedParts: Map<MDFilePartType, Boolean>
    val exportDirectory: MDDocumentData?
    val exportFileType: MDFileType
    val exportFileName: String

    val exportProgress: ExportProgress?

    val isExportRunning: Boolean
        get() = exportProgress is ExportProgress.Running

    val isExportError: Boolean
        get() = exportProgress is ExportProgress.Error

    val isExportIdle: Boolean
        get() = exportProgress == null

    val validExportData: Boolean
        get() = selectedParts.count {
            it.value
        } > 0 && exportFileName.isNotBlank() && exportDirectory != null && exportFileType != MDFileType.Unknown
}

class MDExportToFileMutableUiState : MDExportToFileUiState, MDMutableUiState() {
    override var exportPreferences: MDExportToFilePreferences by mutableStateOf(
        MDExportToFilePreferences.Default
    )

    override val selectedParts: SnapshotStateMap<MDFilePartType, Boolean> = mutableStateMapOf<MDFilePartType, Boolean>().apply {
        putAll(MDFilePartType.entries.associateWith { true }) // TODO, remove this line
    }
    override var exportDirectory: MDDocumentData? by mutableStateOf(null)
    override var exportFileType: MDFileType by mutableStateOf(MDFileType.Json)
    override var exportFileName: String by mutableStateOf(INVALID_TEXT)
    override var exportProgress: ExportProgress? by mutableStateOf(null)
}
