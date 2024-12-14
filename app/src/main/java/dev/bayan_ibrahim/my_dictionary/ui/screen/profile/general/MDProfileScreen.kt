package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.component.MDProfileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme

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
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BackupAndRestoreGroup(
                onClickImportFromFile = uiActions::navigateToImportFromFile,
                onClickExportToFile = uiActions::navigateToExportToFile,
                onCLickAutoImport = uiActions::navigateToAutoBackup,
            )
            ThemeGroup(uiActions::navigateToAppTheme)
        }
    }
}

@Composable
private fun BackupAndRestoreGroup(
    onClickImportFromFile: () -> Unit,
    onClickExportToFile: () -> Unit,
    onCLickAutoImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDHorizontalCardGroup(
        modifier = modifier,
        title = {
            Text("Backup & Restore") // TODO,
        }
    ) {
        item(
            onClick = onClickImportFromFile,
            leadingIcon = {
                Icon(Icons.Default.Face, null) // TODO, icon res
            }
        ) {
            Text("Import from file")// TODO, string res
        }

        item(
            onClick = onClickExportToFile,
            enabled = false, // TODO, not implemented yet
            leadingIcon = {
                Icon(Icons.Default.Face, null) // TODO, icon res
            }
        ) {
            Text("Export to file")// TODO, string res
        }

        item(
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

@Composable
private fun ThemeGroup(
    onClickAppTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDHorizontalCardGroup(
        title = {
            // TODO, string resource
            Text("App Theme")
        }
    ) {
        item (
            onClick = onClickAppTheme,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Star, contentDescription = null) // TODO, icon res
            }
        ) {
            Text("Theme")
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
                            override fun navigateToAppTheme() {}
                        },
                        object : MDProfileBusinessUiActions {},
                    )
                )
            }
        }
    }
}