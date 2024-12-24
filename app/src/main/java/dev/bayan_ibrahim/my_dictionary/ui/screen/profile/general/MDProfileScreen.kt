package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.checkboxItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.component.MDProfileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDProfileScreen(
    uiState: MDProfileUiState,
    userPreferences: MDUserPreferences,
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
                onClickSync = uiActions::navigateToSync,
            )
            ThemeGroup(uiActions::navigateToAppTheme)
            WordsListGroup(
                isLiveTemplateEnabled = userPreferences.liveMemorizingProbability,
                onToggleLiveTemplate = uiActions::onToggleLiveTemplate
            )
        }
    }
}

@Composable
private fun BackupAndRestoreGroup(
    onClickImportFromFile: () -> Unit,
    onClickExportToFile: () -> Unit,
    onClickSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDHorizontalCardGroup(
        modifier = modifier,
        title = {
            Text("Backup & Restore") // TODO, string res
        }
    ) {
        item(
            onClick = onClickImportFromFile,
            leadingIcon = {
                MDIcon(MDIconsSet.ImportFromFile, contentDescription = null) 
            }
        ) {
            Text("Import from file")// TODO, string res
        }

        item(
            onClick = onClickExportToFile,
            enabled = false, // TODO, not implemented yet
            leadingIcon = {
                MDIcon(MDIconsSet.ExportToFile, contentDescription = null) 
            }
        ) {
            Text("Export to file")// TODO, string res
        }

        item(
            onClick = onClickSync,
            enabled = false, // TODO, not implemented yet
            leadingIcon = {
                MDIcon(MDIconsSet.Sync, contentDescription = null) 
            },
        ) {
            Text("Sync")// TODO, string res
        }
    }
}

@Composable
private fun ThemeGroup(
    onClickAppTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDHorizontalCardGroup(
        modifier = modifier,
        title = {
            // TODO, string resource
            Text("App Theme")
        }
    ) {
        item(
            onClick = onClickAppTheme,
            leadingIcon = {
                MDIcon(icon = MDIconsSet.Colors, contentDescription = null) 
            }
        ) {
            // TODO, string resource
            Text("Theme")
        }
    }
}

@Composable
private fun WordsListGroup(
    isLiveTemplateEnabled: Boolean,
    onToggleLiveTemplate: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    MDHorizontalCardGroup(
        modifier = modifier,
        title = {
            Text("Words List") // TODO, string res
        },
    ) {
        checkboxItem (
            checked = isLiveTemplateEnabled,
            onClick = {
                onToggleLiveTemplate(!isLiveTemplateEnabled)
            },
            subtitle = {
                Text(
                    if (isLiveTemplateEnabled) {
                        "probability would be calculated in real time"
                    } else {
                        "probability would be calculated once for each word"
                    }
                )
            }
        ) {
            Text("Memorize Probability live preview") // TODO, string res
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
                    userPreferences = MDUserPreferences(),
                    uiActions = MDProfileUiActions(
                        object : MDProfileNavigationUiActions {
                            override fun navigateToImportFromFile() {}
                            override fun navigateToExportToFile() {}
                            override fun navigateToSync() {}
                            override fun navigateToAppTheme() {}
                        },
                        object : MDProfileBusinessUiActions {
                            override fun onToggleLiveTemplate(liveTemplate: Boolean) {
                                TODO("Not yet implemented")
                            }
                        },
                    )
                )
            }
        }
    }
}