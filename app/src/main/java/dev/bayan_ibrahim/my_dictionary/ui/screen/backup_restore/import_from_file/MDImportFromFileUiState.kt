package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy

interface MDImportFromFileUiState : MDUiState {
    val fileData: MDDocumentData?
    val fileType: MDFileType
    val isFetchingAvailablePartsInProgress: Boolean
    val selectedParts: Map<MDFilePartType, Boolean>
    val fileVersion: Int
    val existedWordStrategy: MDPropertyConflictStrategy
    val corruptedWordStrategy: MDPropertyCorruptionStrategy

    val validSelectedFileParts: Boolean

    val extraTagsStrategy: MDExtraTagsStrategy
}

class MDImportFromFileMutableUiState : MDImportFromFileUiState, MDMutableUiState() {
    override var fileData: MDDocumentData? by mutableStateOf(null)
    override var fileType: MDFileType by mutableStateOf(MDFileType.Unknown)
    override var isFetchingAvailablePartsInProgress: Boolean by mutableStateOf(false)
    override val selectedParts: SnapshotStateMap<MDFilePartType, Boolean> = mutableStateMapOf()
    override var fileVersion: Int by mutableIntStateOf(-1)
    override var existedWordStrategy: MDPropertyConflictStrategy by mutableStateOf(MDPropertyConflictStrategy.IgnoreProperty)
    override var corruptedWordStrategy: MDPropertyCorruptionStrategy by mutableStateOf(MDPropertyCorruptionStrategy.IgnoreProperty)
    override val validSelectedFileParts: Boolean
        get() = fileData != null && selectedParts.count { it.value } > 0
    override var extraTagsStrategy: MDExtraTagsStrategy by mutableStateOf(MDExtraTagsStrategy.All)
}