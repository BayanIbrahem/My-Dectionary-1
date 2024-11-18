package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDExportToFileScreen(
    uiState: MDExportToFileUiState,
    uiActions: MDExportToFileUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        contentWindowInsets = WindowInsets(8.dp, 8.dp, 8.dp, 8.dp),
//       topBar = {
//           MDExportToFileTopAppBar()
//       },
    ) {
    }
}

@Preview
@Composable
private fun MDExportToFileScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDExportToFileScreen(
                    uiState = MDExportToFileMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = MDExportToFileUiActions(
                        object : MDExportToFileNavigationUiActions {
                        },
                        object : MDExportToFileBusinessUiActions {},
                    )
                )
            }
        }
    }
}
