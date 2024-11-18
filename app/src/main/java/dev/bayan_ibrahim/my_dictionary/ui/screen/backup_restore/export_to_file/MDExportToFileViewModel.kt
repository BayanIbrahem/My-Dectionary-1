package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import javax.inject.Inject

@HiltViewModel
class MDExportToFileViewModel @Inject constructor(
//    private val repo: MDExportToFileRepo
) : ViewModel() {
    private val _uiState: MDExportToFileMutableUiState = MDExportToFileMutableUiState()
    val uiState: MDExportToFileUiState = _uiState
    fun initWithNavArgs(args: MDDestination.ExportToFile) {

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
    }
}
