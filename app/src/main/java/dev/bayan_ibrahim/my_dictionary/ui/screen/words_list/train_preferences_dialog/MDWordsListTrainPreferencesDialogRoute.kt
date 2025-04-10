package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navArgument
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

@Composable
fun MDWordsListTrainPreferencesDialogRoute(
    showDialog: Boolean,
    onNavigateToTrainScreen: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDWordsListTrainPreferencesViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.initWithNavArgs()
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDWordsListTrainPreferencesNavigationUiActions{

                override fun onDismissDialog() {
                    onDismissDialog()
                }

                override fun navigateToTrainScreen() {
                    onDismissDialog()
                    onNavigateToTrainScreen()
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDWordsListTrainPreferencesDialog(
        showDialog = showDialog,
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}
