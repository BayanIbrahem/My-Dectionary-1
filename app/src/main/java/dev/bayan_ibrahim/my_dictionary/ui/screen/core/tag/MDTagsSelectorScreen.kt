package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDTagsSelectorScreen(
    uiState: MDTagsSelectorUiState,
    uiActions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit) = {
        Text(
            // TODO, string res
            "Tags Selector"
        )
    },
    lazyState: LazyListState = rememberLazyListState(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { uiActions.onPop() }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                title = title,
                actions = {
                    if (uiState.isSelectEnabled)
                        IconButton(
                            onClick = { uiActions.onConfirmSelectedTags() }
                        ) {
                            MDIcon(MDIconsSet.Check)
                        }
                }
            )
        },
    ) {
        MDTagsSelectorContent(
            uiState = uiState,
            uiActions = uiActions,
            modifier = modifier.padding(it),
            lazyState = lazyState
        )
    }
}
