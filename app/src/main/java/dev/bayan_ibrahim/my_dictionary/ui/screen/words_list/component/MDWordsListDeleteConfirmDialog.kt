package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDialogColors
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDialogDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDWordsListDeleteConfirmDialog(
    showDialog: Boolean,
    isDeleteRunning: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String,
    confirmDeleteMessage: String,
    runningDeleteMessage: String,
    modifier: Modifier = Modifier,
) {
    val onDismissRequest: () -> Unit by remember {
        derivedStateOf {
            { if (!isDeleteRunning) onCancel() }
        }
    }
    val deleteMessage by remember(isDeleteRunning) {
        derivedStateOf {
            if (isDeleteRunning) runningDeleteMessage else confirmDeleteMessage
        }
    }
    MDAlertDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge
                )// TODO, string res
                if (isDeleteRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        },
        modifier = modifier,
        actions = {
            MDAlertDialogActions(
                onPrimaryClick = onConfirm,
                onSecondaryClick = onCancel,
                primaryActionLabel = "Delete",
                colors = MDDialogDefaults.colors(
                    primaryActionColor = MaterialTheme.colorScheme.error,
                ),
                dismissOnPrimaryClick = true,
                dismissOnSecondaryClick = false,
                primaryClickEnabled = !isDeleteRunning,
                secondaryClickEnabled = !isDeleteRunning,
            )
        },
    ) {
        Text(
            text = deleteMessage,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )// TODO, string res
    }
}

@Preview
@Composable
private fun MDWordsListDeleteConfirmDialogPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordsListDeleteConfirmDialog(
                    showDialog = true,
                    isDeleteRunning = true,
                    onCancel = {},
                    onConfirm = {},
                    title = "Delete Title",
                    confirmDeleteMessage = "Confirm message",
                    runningDeleteMessage = "Running delete message"
                )
            }
        }
    }
}