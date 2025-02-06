package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.FileManager
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.domain.repo.ExportToFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.util.MDExportToFilePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDExportToFileViewModel @Inject constructor(
    private val wordRepo: WordRepo,
    private val exportToFileRepo: ExportToFileRepo,
    private val fileManager: FileManager,
) : ViewModel() {
    private val _uiState: MDExportToFileMutableUiState = MDExportToFileMutableUiState()
    val uiState: MDExportToFileUiState = _uiState
    fun initWithNavArgs(args: MDDestination.ExportToFile) {
        _uiState.onExecute {
            _uiState.exportPreferences = args.preferences
            true
        }
    }

    fun getUiActions(
        navActions: MDExportToFileNavigationUiActions,
    ): MDExportToFileUiActions = MDExportToFileUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDExportToFileNavigationUiActions,
    ): MDExportToFileBusinessUiActions = object : MDExportToFileBusinessUiActions {
        override fun onToggleSelectAvailablePart(type: MDFilePartType, selected: Boolean) {
            _uiState.selectedParts[type] = selected
        }

        override fun onStartExportProcess() {
            val dir = uiState.exportDirectory
            val type = uiState.exportFileType
            val name = uiState.exportFileName
            val selectedParts = uiState.selectedParts.mapNotNull { if (it.value) it.key else null }.toSet()
            if (dir != null && type != MDFileType.Unknown && name.isNotBlank() && selectedParts.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("export", "export started...")
                    val wordsIds = getWordsIdsOfPreferences()
                    startExportProgress(
                        wordsIds = wordsIds,
                        selectedParts = selectedParts,
                        dir = dir,
                        type = type,
                        name = name
                    )
                }
            }
        }

        override fun onSelectExportFileType(type: MDFileType) {
            _uiState.exportFileType = type
        }

        override fun onExportFileNameChange(newName: String) {
            _uiState.exportFileName = newName
        }

        override fun onExportDirectoryChange(uri: Uri) {
            viewModelScope.launch {
                val fileData = fileManager.getDocumentData(uri).getOrNull() ?: return@launch
                _uiState.exportDirectory = fileData
            }
        }

        private var exportJob: Job? = null
        override fun onCancelExport() {
            exportJob?.cancel()
            exportJob = null
            _uiState.exportProgress = null
        }

        private fun startExportProgress(
            wordsIds: Set<Long>,
            selectedParts: Set<MDFilePartType>,
            dir: MDDocumentData,
            type: MDFileType,
            name: String,
        ) {
            exportJob?.cancel()
            exportJob = viewModelScope.launch {
                exportToFileRepo.export(
                    wordsIds = wordsIds,
                    parts = selectedParts,
                    exportDirectory = dir,
                    exportFileType = type,
                    exportFileName = name
                ).collect { progress ->
                    _uiState.exportProgress = progress
                }
            }
        }
    }

    private suspend fun getWordsIdsOfPreferences(
        preferences: MDExportToFilePreferences = uiState.exportPreferences,
    ): Set<Long> = when (preferences) {
        MDExportToFilePreferences.All -> wordRepo.getWordsIdsOf().first()
        is MDExportToFilePreferences.Tags -> wordRepo.getWordsIdsOf(tags = preferences.tags.toSet()).first()
        is MDExportToFilePreferences.Languages -> wordRepo.getWordsIdsOf(languages = preferences.codes.toSet()).first()
        is MDExportToFilePreferences.WordsClasses -> wordRepo.getWordsIdsOf(wordsClasses = preferences.ids.toSet()).first()
        is MDExportToFilePreferences.Words -> preferences.ids.toSet()
    }

}
