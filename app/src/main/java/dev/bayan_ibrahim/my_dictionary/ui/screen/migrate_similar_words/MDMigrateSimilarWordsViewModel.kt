package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_similar_words


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import javax.inject.Inject

@HiltViewModel
class MDMigrateSimilarWordsViewModel @Inject constructor(
//    private val repo: MDMigrateSimilarWordsRepo
) : ViewModel() {
    private val _uiState: MDMigrateSimilarWordsMutableUiState = MDMigrateSimilarWordsMutableUiState()
    val uiState: MDMigrateSimilarWordsUiState = _uiState
    fun initWithNavArgs(args: MDDestination) {

    }

    fun getUiActions(
        navActions: MDMigrateSimilarWordsNavigationUiActions,
    ): MDMigrateSimilarWordsUiActions = MDMigrateSimilarWordsUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDMigrateSimilarWordsNavigationUiActions,
    ): MDMigrateSimilarWordsBusinessUiActions = object : MDMigrateSimilarWordsBusinessUiActions {
    }
}
