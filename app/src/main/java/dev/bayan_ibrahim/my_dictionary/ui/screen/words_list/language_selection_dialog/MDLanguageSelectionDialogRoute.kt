package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

@Composable
fun MDLanguageSelectionDialogRoute(
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MDLanguageSelectionDialogViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.initWithNavArgs()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navActions by remember {
        derivedStateOf {
            object : MDLanguageSelectionDialogNavigationUiActions{
                override fun onDismissRequest() {
                    onDismissDialog()
                }
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDLanguageSelectionDialogScreen(
        showDialog = showDialog,
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}
