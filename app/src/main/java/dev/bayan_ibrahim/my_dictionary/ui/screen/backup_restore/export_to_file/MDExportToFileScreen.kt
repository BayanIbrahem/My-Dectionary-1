package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDialogDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.core.design_system.progress_indicator.linear.MDLinearProgressIndicator
import dev.bayan_ibrahim.my_dictionary.core.ui.MDPlainTooltip
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.data.ExportProgress
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.ExportToFileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFilePartsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDOptionSelectionGroup
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlin.math.roundToInt

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
                val visible by remember(scrollState, uiState) {
                    derivedStateOf {
                        !scrollState.canScrollForward && uiState.validExportData && uiState.isExportIdle
                    }
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally(),
                ) {
                    Text(firstCapStringResource(R.string.export))
                }
            }
        }
    ) {
        ExportProgressDialog(
            exportProgress = uiState.exportProgress,
            onCancelProgress = uiActions::onCancelExport
        )
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Column {
//                Text(
//                    text = eachFirstCapStringResource(R.string.export_preferences),
//                    style = MDHorizontalCardDefaults.styles().titleStyle
//                )
//                MDPlainTooltip(
//                    tooltipContent = {
//                        Column {
//                            Text("Move to each screen to select export data")
//                            Row {
//                                Text("* Words: ", fontWeight = FontWeight.Bold)
//                                Text("Words list screen")
//                            }
//                            Row {
//                                Text("* Languages: ", fontWeight = FontWeight.Bold)
//                                Text("Word spaces screen")
//                            }
//                            // TODO, add other options, and make options navigable
//                        }
//                    }
//                ) {
//                    Text(
//                        text = "Want select another strategy?",
//                        style = MDHorizontalCardDefaults.styles().subtitleStyle
//                    )
//                }
//                InputChip(
//                    selected = false,
//                    enabled = false,
//                    onClick = {},
//                    label = {
//                        Text(uiState.exportPreferences.label)
//                    }
//                )
//            }

            MDFilePartsSelector(
                selectedParts = uiState.selectedParts,
                onToggleAvailablePart = uiActions::onToggleSelectAvailablePart
            )
            MDOptionSelectionGroup(
                title = firstCapStringResource(R.string.export_file_type),
                availableOptions = MDFileType.validEntries,
                onSelectOption = uiActions::onSelectExportFileType,
                selectedOption = uiState.exportFileType
            )
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                result.data?.data?.let(uiActions::onExportDirectoryChange)
            }
            val chooseDirectory = firstCapStringResource(R.string.choose_directory)
            val directoryValue by remember(uiState) {
                derivedStateOf {
                    uiState.exportDirectory?.name?.plus('/') ?: chooseDirectory
                }
            }
            val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
            val directoryColor by remember(uiState) {
                derivedStateOf {
                    if (uiState.exportDirectory == null) {
                        onPrimaryContainer.copy(alpha = 0.5f)
                    } else {
                        onPrimaryContainer
                    }
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
                            color = directoryColor,
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

@Composable
private fun ExportProgressDialog(
    exportProgress: ExportProgress?,
    onCancelProgress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val show by remember(exportProgress) {
        derivedStateOf {
            exportProgress != null
        }
    }
    MDAlertDialog(
        modifier = modifier
            .widthIn(250.dp, 300.dp)
            .fillMaxWidth(),
        showDialog = show,
        onDismissRequest = {},
        contentModifier = Modifier.padding(16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MDIcon(MDIconsSet.ExportToFile) // TODO, icon res
                val text = when (exportProgress) {
                    is ExportProgress.Done -> firstCapStringResource(R.string.export_done)
                    is ExportProgress.Error -> firstCapStringResource(R.string.export_error)
                    is ExportProgress.Running -> firstCapStringResource(
                        R.string.export_running_x_of_y,
                        exportProgress.partIndex.inc(),
                        exportProgress.availableParts.count()
                    )
                    null -> ""
                }
                Text(text)
            }
        },
        actions = {
            MDAlertDialogActions(
                primaryClickEnabled = true,
                onPrimaryClick = onCancelProgress,
                primaryActionLabel = if (exportProgress?.isRunning == true) {
                    firstCapStringResource(R.string.cancel)
                } else {
                    firstCapStringResource(R.string.close)
                },
                hasPrimaryAction = true,
                hasSecondaryAction = false,
                hasTertiaryAction = false,
                colors = MDDialogDefaults.colors(primaryActionColor = MaterialTheme.colorScheme.error),
            )
        },
    ) {
        when (exportProgress) {
            is ExportProgress.Done -> {
                Column {
                    Text(
                        text = firstCapStringResource(R.string.export_done),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = firstCapStringResource(R.string.output),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val path by remember(exportProgress) {
                            derivedStateOf {
                                exportProgress.outputFile.filePath ?: "${exportProgress.outputDir.name}/${exportProgress.outputFile.name}"
                            }
                        }
                        Text(
                            text = path, // TODO, trim from start if long instead of trim from end
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        IconButton(
                            onClick = {
                                // TODO, open export file location
                            }
                        ) {
                            MDIcon(MDIconsSet.ExportToFile) // TODO, icon res
                        }
                    }
                }
            }

            is ExportProgress.Error -> {
                Column {
                    Text(
                        text = exportProgress.errorName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = exportProgress.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is ExportProgress.Running -> {
                Column {
                    val parts by remember(exportProgress.availableParts) {
                        derivedStateOf {
                            exportProgress.availableParts.sorted()
                        }
                    }
                    parts.forEach { part ->
                        val isCurrentPart by remember(part, exportProgress.currentFilePart) {
                            derivedStateOf { exportProgress.currentFilePart == part }
                        }
                        val scale by animateFloatAsState(if (isCurrentPart) 1f else 0.67f)
                        val alpha by animateFloatAsState(if (isCurrentPart) 1f else 0.5f)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    this.scaleX = scale
                                    this.scaleY = scale
                                    this.alpha = alpha
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(part.label)
                            AnimatedVisibility(
                                modifier = Modifier.weight(1f),
                                visible = isCurrentPart,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    MDLinearProgressIndicator(
                                        progress = exportProgress.progress,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${exportProgress.progress.times(100).roundToInt()}%",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            null -> {}
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
                            override fun onCancelExport() {}
                        },
                    )
                )
            }
        }
    }
}
