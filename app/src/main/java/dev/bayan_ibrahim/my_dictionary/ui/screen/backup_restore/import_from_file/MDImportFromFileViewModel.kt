package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingMutableSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsImpl
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
) : ViewModel() {
    private val _uiState: MDImportFromFileMutableUiState = MDImportFromFileMutableUiState()
    val uiState: MDImportFromFileUiState = _uiState
    fun initWithNavArgs(args: MDDestination.ImportFromFile) {
        _uiState.onExecute { true }
    }

    private val _importSummary: MDFileProcessingMutableSummary = MDFileProcessingMutableSummary()
    private val importSummaryActions = MDFileProcessingSummaryActionsImpl(_importSummary)
    val importSummary: MDFileProcessingSummary = _importSummary

    private var fileProcessJob: Job? = null

    fun getUiActions(
        navActions: MDImportFromFileNavigationUiActions,
    ): MDImportFromFileUiActions = MDImportFromFileUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDImportFromFileNavigationUiActions,
    ): MDImportFromFileBusinessUiActions = object : MDImportFromFileBusinessUiActions {
        override fun onSelectFile(fileData: MDFileData) {
            _uiState.selectedFileData = fileData
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.fileValidationInProgress = true

                _uiState.validFile = repo.checkFileIfValid(fileData)

                _uiState.fileValidationInProgress = false

                Log.d("file_picker", "pick file data: $fileData, valid: ${_uiState.validFile}")
            }
        }

        override fun onSelectFileType(selectedFileType: MDFileType?) {
            _uiState.selectedFileType = selectedFileType
        }

        override fun onOverrideFileTypeCheckChange(checked: Boolean) {
            _uiState.overrideFileTypeChecked = checked
        }

        override fun onChangeCorruptedWordStrategy(strategy: MDPropertyCorruptionStrategy) {
            _uiState.corruptedWordStrategy = strategy
        }

        override fun onChangeExistedWordStrategy(strategy: MDPropertyConflictStrategy) {
            _uiState.existedWordStrategy = strategy
        }

        override fun onStartImportProcess() {
            if (uiState.validFile || uiState.fileValidationInProgress) {
                fileProcessJob?.cancel()
                fileProcessJob = viewModelScope.launch(Dispatchers.IO) {
                    uiState.selectedFileData?.let { data ->
                        repo.processFile(
                            fileData = data,
                            existedWordStrategy = uiState.existedWordStrategy,
                            corruptedWordStrategy = uiState.corruptedWordStrategy,
                            outputSummaryActions = importSummaryActions,
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
}
