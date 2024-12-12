package dev.bayan_ibrahim.my_dictionary.ui.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDField
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroup
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.component.MDProfileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDProfileScreen(
    uiState: MDProfileUiState,
    uiActions: MDProfileUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDProfileTopAppBar()
        },
    ) {
        BackupAndRestoreGroup(
            onClickImportFromFile = uiActions::navigateToImportFromFile,
            onClickExportToFile = uiActions::navigateToExportToFile,
            onCLickAutoImport = uiActions::navigateToAutoBackup,
        )
    }
}

@Composable
private fun BackupAndRestoreGroup(
    onClickImportFromFile: () -> Unit,
    onClickExportToFile: () -> Unit,
    onCLickAutoImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDFieldsGroup(
        modifier = modifier,
        title = {
            Text("Backup & Restore") // TODO,
        }
    ) {
        MDField(
            onClick = onClickImportFromFile,
            leadingIcon = {
                Icon(Icons.Default.Face, null) // TODO, icon res
            }
        ) {
            Text("Import from file")// TODO, string res
        }

        MDField(
            onClick = onClickExportToFile,
            enabled = false, // TODO, not implemented yet
            leadingIcon = {
                Icon(Icons.Default.Face, null) // TODO, icon res
            }
        ) {
            Text("Export to file")// TODO, string res
        }

        MDField(
            onClick = onCLickAutoImport,
            enabled = false, // TODO, not implemented yet
            leadingIcon = {
                Icon(Icons.Default.Face, null) // TODO, icon res
            },
        ) {
            Text("Auto Import")// TODO, string res
        }
    }
}

@Preview
@Composable
private fun MDProfileScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDProfileScreen(
                    uiState = MDProfileMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = MDProfileUiActions(
                        object : MDProfileNavigationUiActions {
                            override fun navigateToImportFromFile() {}
                            override fun navigateToExportToFile() {}
                            override fun navigateToAutoBackup() {}
                        },
                        object : MDProfileBusinessUiActions {},
                    )
                )
            }
        }
    }
}