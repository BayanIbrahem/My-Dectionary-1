package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryStatus
import dev.bayan_ibrahim.my_dictionary.domain.model.readFileData
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.ImportFromFileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFileFileIdentifier
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFileStrategyRadioGroup

val corruptedFields = listOf(
    MDFileStrategy.Ignore,
    MDFileStrategy.Abort,
)

@Composable
fun MDImportFromFileScreen(
    uiState: MDImportFromFileUiState,
    summary: MDFileProcessingSummary,
    uiActions: MDImportFromFileUiActions,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        context.readFileData(uri)?.let {
            uiActions.onSelectFile(it)
        }
    }
    val isImportInProgress by remember(summary.status) {
        derivedStateOf {
            summary.status.isRunning
        }
    }

    MDScreen(
        uiState = uiState,
        modifier = modifier,
        contentWindowInsets = WindowInsets(8.dp, 8.dp, 8.dp, 8.dp),
        topBar = {
            ImportFromFileTopAppBar()
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MDFileFileIdentifier(
                selectedFileName = uiState.selectedFileData?.name,
                selectedFileType = uiState.selectedFileType,
                detectedFileType = uiState.detectedFileType,
                fileInputFieldClickable = uiState.fileInputFieldClickable,
                overrideFileTypeChecked = uiState.overrideFileTypeChecked,
                overrideFileTypeEnabled = uiState.overrideFileTypeEnabled,
                onClickFileInputField = {
                    launcher.launch(arrayOf("*/*"))
//                    launcher.launch(MDFileType.entriesMimeType)
                },
                validFile = uiState.validFile,
                fileValidationInProgress = uiState.fileValidationInProgress,
                onSelectFileType = uiActions::onSelectFileType,
                onOverrideFileTypeCheckChange = uiActions::onOverrideFileTypeCheckChange
            )
            MDFileStrategyRadioGroup(
                availableStrategies = corruptedFields,
                selectedStrategy = uiState.corruptedWordStrategy,
                onSelectStrategy = uiActions::onChangeCorruptedWordStrategy,
                title = "Corrupted Word", // TODO, string res
                subtitle = "Action when a corrupted word is found", // TODO, string res
            )
            MDFileStrategyRadioGroup(
                selectedStrategy = uiState.existedWordStrategy,
                onSelectStrategy = uiActions::onChangeExistedWordStrategy,
                title = "Existed Word", // TODO, string res
                subtitle = "Action when a word with the same meaning and translation is already stored", // TODO, string res
            )
            AnimatedVisibility(
                visible = !isImportInProgress
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = uiActions::onStartImportProcess,
                    enabled = uiState.validFile && !uiState.fileValidationInProgress,
                ) {
                    Text("Import File") // TODO, string res
                }
            }
            AnimatedVisibility(
                visible = summary.status != MDFileProcessingSummaryStatus.IDLE
            ) {
                Column {
                    Text("Importing file") // TODO, string res
                    if (summary.status.isRunning) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    val runningTime by remember(summary.processingStartTime, summary.processingEndTime) {
                        derivedStateOf {
                            summary.runningTime
                        }
                    }
                    // TODO, view them better
                    Text(summary.asString(runningTime = runningTime))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = uiActions::onCancelImportProcess,
                    ) {
                        Text("Abort Import") // TODO, string res
                    }
                }
            }
        }
    }
}