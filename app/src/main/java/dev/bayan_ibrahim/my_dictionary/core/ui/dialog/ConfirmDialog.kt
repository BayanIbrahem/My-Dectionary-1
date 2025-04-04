package dev.bayan_ibrahim.my_dictionary.core.ui.dialog

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2Action
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDConfirmDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String,
    confirmMessage: String,
    modifier: Modifier = Modifier,
    isConfirmRunning: Boolean = false,
    runningMessage: String = confirmMessage,
    headerTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryContainer,
    confirmButtonTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryOnSurface,
    cancelButtonTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceVariant,
    confirmButtonLabel: String = firstCapStringResource(R.string.confirm),
    cancelButtonLabel: String = firstCapStringResource(R.string.cancel),
) {
    val onDismissRequest: () -> Unit by remember {
        derivedStateOf {
            { if (!isConfirmRunning) onCancel() }
        }
    }
    val message by remember(isConfirmRunning) {
        derivedStateOf {
            if (isConfirmRunning) runningMessage else confirmMessage
        }
    }
    if (showDialog)
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            MDCard2(
                modifier = modifier,
                headerTheme = headerTheme,
                header = {
                    MDCard2ListItem(
                        title = title,
                        trailingIcon =
                        if (isConfirmRunning) {
                            {

                                CircularProgressIndicator()
                            }
                        } else {
                            null
                        }
                    )
                },
                footer = {
                    MDCard2ActionRow {
                        MDCard2Action(
                            label = cancelButtonLabel,
                            onClick = onCancel,
                            theme = cancelButtonTheme,
                            modifier = Modifier.weight(1f),
                        )
                        MDCard2Action(
                            label = confirmButtonLabel,
                            onClick = onConfirm,
                            theme = confirmButtonTheme,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            ) {
                // if long then it have subtitle style
                val isLong by remember(message) {
                    derivedStateOf { message.length > 150 }
                }
                AnimatedContent(isLong) { isLong ->
                    if (isLong) {
                        val theme = LocalMDCard2ListItemTheme.current
                        MDCard2ListItem(
                            title = {
                                Text(
                                    text = message,
                                    maxLines = Int.MAX_VALUE,
                                    style = theme.subtitleStyle,
                                )
                            },
                        )
                    } else {
                        MDCard2ListItem(title = message, titleMaxLines = Int.MAX_VALUE)
                    }
                }
            }
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