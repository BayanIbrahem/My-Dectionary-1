package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDTagsSelectorContent(
    uiState: MDTagsSelectorUiState,
    uiActions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    lazyState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = lazyState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tagsSelectorLazyContent(uiState, uiActions)
    }
}

