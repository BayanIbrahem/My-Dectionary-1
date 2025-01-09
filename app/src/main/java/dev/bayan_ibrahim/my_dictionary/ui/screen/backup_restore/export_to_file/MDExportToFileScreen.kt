package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.core.ui.MDPlainTooltip
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.ExportToFileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFilePartsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDOptionSelectionGroup
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDExportToFileScreen(
    uiState: MDExportToFileUiState,
    uiActions: MDExportToFileUiActions,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
) {
    val scrollState = rememberScrollState()
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            ExportToFileTopAppBar(onNavigationIconClick = uiActions::onPop)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = uiActions::onStartExportProcess,
            ) {
                MDIcon(MDIconsSet.ExportToFile) // TODO, icon res
                AnimatedVisibility(
                    visible = !scrollState.canScrollForward,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally(),
                ) {
                    Text("Export") // TODO, string res
                }
            }
        }
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            Column {
                Text(
                    text = "Export Preferences",
                    style = MDHorizontalCardDefaults.styles().titleStyle
                )
                MDPlainTooltip(
                    tooltipContent = {
                        Column {
                            Text("Move to each screen to select export data")
                            Row {
                                Text("* Words: ", fontWeight = FontWeight.Bold)
                                Text("Words list screen")
                            }
                            Row {
                                Text("* Languages: ", fontWeight = FontWeight.Bold)
                                Text("Word spaces screen")
                            }
                            // TODO, add other options, and make options navigable
                        }
                    }
                ) {
                    Text(
                        text = "Want select another strategy?",
                        style = MDHorizontalCardDefaults.styles().subtitleStyle
                    )
                }
                InputChip(
                    selected = false,
                    enabled = false,
                    onClick = {},
                    label = {
                        Text(uiState.exportPreferences.label)
                    }
                )
            }

            MDFilePartsSelector(
                selectedParts = uiState.selectedParts,
                onToggleAvailablePart = uiActions::onToggleSelectAvailablePart
            )
            MDOptionSelectionGroup(
                title = "Export file type",
                availableOptions = MDFileType.validEntries,
                onSelectOption = uiActions::onSelectExportFileType,
                selectedOption = uiState.exportFileType
            )
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                result.data?.data?.let(uiActions::onExportDirectoryChange)
            }
            val directoryValue by remember(uiState) {
                derivedStateOf {
                    uiState.exportDirectory?.name?.plus('/') ?: ""
                }
            }
            MDVerticalCard(
                headerModifier = Modifier.height(56.dp),
                headerClickable = true,
                contentModifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                onClickHeader = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        )
                    }
                    launcher.launch(intent)
                },
                header = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MDIcon(MDIconsSet.ExportToFile) // TODO, icon res, directory res
                        Text(
                            text = directoryValue,
                            style = MDTextFieldDefaults.textStyle,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            ) {
                MDBasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcons = {
                        MDIcon(MDIconsSet.ExportToFile) // TODO, file name
                    },
                    value = uiState.exportFileName,
                    suffix = {
                        if (uiState.exportFileType != MDFileType.Unknown) {
                            Text(uiState.exportFileType.typeExtensionLabel)
                        }
                    },
                    onValueChange = uiActions::onExportFileNameChange,
                )
            }
        }
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
                        object : MDExportToFileNavigationUiActions, MDAppNavigationUiActions {
                            override fun onOpenNavDrawer() {}
                            override fun onCloseNavDrawer() {}
                        },
                        object : MDExportToFileBusinessUiActions {
                            override fun onToggleSelectAvailablePart(type: MDFilePartType, selected: Boolean) {}
                            override fun onStartExportProcess() {}
                            override fun onSelectExportFileType(type: MDFileType) {}
                            override fun onExportFileNameChange(newName: String) {}
                            override fun onExportDirectoryChange(uri: Uri) {}
                        },
                    )
                )
            }
        }
    }
}
