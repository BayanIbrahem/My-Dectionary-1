package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFileSize
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileSize
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileType

interface MDImportFromFileUiState : MDUiState {
    val selectedFileData: MDFileData?
    val selectedFileType: MDFileType?
    val detectedFileType: MDFileType?
    val fileInputFieldClickable: Boolean
    val overrideFileTypeChecked: Boolean
    val overrideFileTypeEnabled: Boolean
    val validFile: Boolean
    val fileValidationInProgress: Boolean
    val corruptedWordStrategy: MDFileStrategy
    val existedWordStrategy: MDFileStrategy
}

class MDImportFromFileMutableUiState : MDImportFromFileUiState, MDMutableUiState() {
    override var selectedFileData: MDFileData? by mutableStateOf(null)
    override var selectedFileType: MDFileType? by mutableStateOf(null)
    override var detectedFileType: MDFileType? by mutableStateOf(null)
    override var fileInputFieldClickable: Boolean by mutableStateOf(true)
    override var overrideFileTypeChecked: Boolean by mutableStateOf(false)
    override var overrideFileTypeEnabled: Boolean by mutableStateOf(true)
    override var validFile: Boolean by mutableStateOf(true)
    override var fileValidationInProgress: Boolean by mutableStateOf(false)
    override var corruptedWordStrategy: MDFileStrategy by mutableStateOf(MDFileStrategy.Ignore)
    override var existedWordStrategy: MDFileStrategy by mutableStateOf(MDFileStrategy.Ignore)
}
