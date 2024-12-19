package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState

data object MDScreenDefaults {
    val paddingWindowInsets = WindowInsets(8.dp, 8.dp, 8.dp, 8.dp)
    val contentWindowInsets
        @Composable
        get() = ScaffoldDefaults.contentWindowInsets.add(
            paddingWindowInsets
        )
}

@Composable
fun MDScreen(
    uiState: MDUiState,
    modifier: Modifier = Modifier,
    invalidDataMessage: String = "Invalid Data", // TODO, string res
    contentAlignment: Alignment = Alignment.TopCenter,
    showFloatingActionButtonOnLoading: Boolean = false,
    showFloatingActionButtonOnError: Boolean = false,
    contentWindowInsets: WindowInsets = MDScreenDefaults.contentWindowInsets,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: (@Composable () -> Unit) = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val showFab by remember(
        key1 = uiState,
        key2 = showFloatingActionButtonOnLoading,
        key3 = showFloatingActionButtonOnError,
    ) {
        derivedStateOf {
            (!uiState.isLoading || showFloatingActionButtonOnLoading)
                    || (uiState.error == null || showFloatingActionButtonOnError)
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        contentWindowInsets = contentWindowInsets,
        floatingActionButton = {
            if (showFab) {
                floatingActionButton()
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = contentAlignment
        ) {
            if (uiState.isLoading) {
                Dialog(onDismissRequest = {}) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            } else if (uiState.error != null) {
                TitledMessage(titleMessage = "Error", bodyMessage = uiState.error ?: "")// TODO, string res
            } else if (uiState.validData) {
                content()
            } else { // invalid data
                TitledMessage(
                    titleMessage = "Invalid Data",
                    bodyMessage = invalidDataMessage
                ) // TODO, string res
            }
        }
    }
}

@Composable
private fun BoxScope.TitledMessage(titleMessage: String, bodyMessage: String) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(titleMessage, style = MaterialTheme.typography.titleLarge)
        Text(bodyMessage, style = MaterialTheme.typography.bodyLarge)
    }
}