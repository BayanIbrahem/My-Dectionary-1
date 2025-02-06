package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.FileManager
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingMutableSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsImpl
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.repo.ImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MDImportFromFileViewModel @Inject constructor(
    private val repo: ImportFromFileRepo,
    @Named("available_versions")
    val availableVersions: List<Int>,
    private val fileManager: FileManager,
) : ViewModel() {
    private val _uiState: MDImportFromFileMutableUiState = MDImportFromFileMutableUiState()
    val uiState: MDImportFromFileUiState = _uiState

    private val _importSummary: MDFileProcessingMutableSummary = MDFileProcessingMutableSummary()
    private val importSummaryActions = MDFileProcessingSummaryActionsImpl(_importSummary)
    val importSummary: MDFileProcessingSummary = _importSummary

    private var fileProcessJob: Job? = null

    private var lastSelectedTagsSnapshot: List<Tag> = emptyList()

    fun initWithNavArgs(args: MDDestination.ImportFromFile) {
        _uiState.onExecute {
            lastSelectedTagsSnapshot = emptyList()
            true
        }
    }

    fun getUiActions(
        navActions: MDImportFromFileNavigationUiActions,
    ): MDImportFromFileUiActions = MDImportFromFileUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private var getAvailablePartsJob: Job? = null
    private fun getBusinessUiActions(
        navActions: MDImportFromFileNavigationUiActions,
    ): MDImportFromFileBusinessUiActions = object : MDImportFromFileBusinessUiActions {
        override fun onPickFile(uri: Uri) {
            viewModelScope.launch {
                val fileData = fileManager.getDocumentData(uri).getOrNull() ?: return@launch

                _uiState.fileData = fileData
                getAvailablePartsJob?.cancel()
                getAvailablePartsJob = launch(Dispatchers.IO) {
                    _uiState.isFetchingAvailablePartsInProgress = true

                    _uiState.selectedParts.setAll(
                        repo.getAvailablePartsInFile(
                            fileData = fileData,
                            outputSummaryActions = importSummaryActions
                        ).associateWith { true }
                    )

                    _uiState.isFetchingAvailablePartsInProgress = false

                    Log.d("file_picker", "pick file data: $fileData, valid: ${_uiState.isFetchingAvailablePartsInProgress}")
                }
            }
        }

        override fun onRemoveFile() {
            _uiState.fileData = null
            _uiState.selectedParts.clear()
        }

        override fun onToggleSelectAvailablePart(type: MDFilePartType, selected: Boolean) {
            if (type in _uiState.selectedParts) {
                _uiState.selectedParts[type] = selected
            }
        }

        override fun onChangeCorruptedWordStrategy(strategy: MDPropertyCorruptionStrategy) {
            _uiState.corruptedWordStrategy = strategy
        }

        override fun onChangeExistedWordStrategy(strategy: MDPropertyConflictStrategy) {
            _uiState.existedWordStrategy = strategy
        }

        override fun onChangeExtraTagsStrategy(strategy: MDExtraTagsStrategy) {
            _uiState.extraTagsStrategy = strategy
        }

        override fun onStartImportProcess() {
            if (uiState.validSelectedFileParts) {
                fileProcessJob?.cancel()
                fileProcessJob = viewModelScope.launch(Dispatchers.IO) {
                    uiState.fileData?.let { data ->
                        repo.processFile(
                            fileData = data,
                            existedWordStrategy = uiState.existedWordStrategy,
                            corruptedWordStrategy = uiState.corruptedWordStrategy,
                            outputSummaryActions = importSummaryActions,
                            extraTags = lastSelectedTagsSnapshot,
                            extraTagsStrategy = uiState.extraTagsStrategy,
                            allowedFileParts = uiState.selectedParts.mapNotNull { if (it.value) it.key else null }.toSet(),
                        )
                    }
                }
            }
        }

        override fun onCancelImportProcess() {
            fileProcessJob?.cancel()
            importSummaryActions.reset()
        }
    }

    fun onUpdateSelectedTags(selectedTags: List<Tag>) {
        lastSelectedTagsSnapshot = selectedTags
    }
}
