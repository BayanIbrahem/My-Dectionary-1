package dev.bayan_ibrahim.my_dictionary.core.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDDeleteConfirmDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String,
    confirmDeleteMessage: String,
    modifier: Modifier = Modifier,
    isDeleteRunning: Boolean = false,
    runningDeleteMessage: String = confirmDeleteMessage,
    headerTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.ErrorContainer,
    confirmButtonTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.ErrorOnSurface,
    cancelButtonTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceVariant,
    confirmButtonLabel: String = firstCapStringResource(R.string.delete),
    cancelButtonLabel: String = firstCapStringResource(R.string.cancel),
) {
    MDConfirmDialog(
        showDialog = showDialog,
        onConfirm = onConfirm,
        onCancel = onCancel,
        title = title,
        confirmMessage = confirmDeleteMessage,
        modifier = modifier,
        isConfirmRunning = isDeleteRunning,
        runningMessage = runningDeleteMessage,
        headerTheme = headerTheme,
        confirmButtonTheme = confirmButtonTheme,
        cancelButtonTheme = cancelButtonTheme,
        confirmButtonLabel = confirmButtonLabel,
        cancelButtonLabel = cancelButtonLabel,
    )
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
                MDDeleteConfirmDialog(
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