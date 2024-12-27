package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import javax.inject.Inject

@HiltViewModel
class MDMigrateTagsViewModel @Inject constructor(
//    private val repo: MDMigrateTagsRepo
) : ViewModel() {
    private val _uiState: MDMigrateTagsMutableUiState = MDMigrateTagsMutableUiState()
    val uiState: MDMigrateTagsUiState = _uiState
    fun initWithNavArgs(args: MDDestination) {

    }

    fun getUiActions(
        navActions: MDMigrateTagsNavigationUiActions,
    ): MDMigrateTagsUiActions = MDMigrateTagsUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDMigrateTagsNavigationUiActions,
    ): MDMigrateTagsBusinessUiActions = object : MDMigrateTagsBusinessUiActions {
    }
}
