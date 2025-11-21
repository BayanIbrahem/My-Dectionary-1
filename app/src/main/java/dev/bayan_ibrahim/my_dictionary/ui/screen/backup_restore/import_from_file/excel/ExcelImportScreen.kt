package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file.excel


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
fun ExcelImportScreen(
    uiState: ExcelImportUiState,
    uiActions: ExcelImportUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
//       topBar = {
//           ExcelImportTopAppBar()
//       },
    ) {
    }
}

@Preview
@Composable
private fun ExcelImportScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ExcelImportScreen(
                    uiState = ExcelImportMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = ExcelImportUiActions(
                        object : ExcelImportNavigationUiActions {
                        },
                        object : ExcelImportBusinessUiActions {},
                    )
                )
            }
        }
    }
}
