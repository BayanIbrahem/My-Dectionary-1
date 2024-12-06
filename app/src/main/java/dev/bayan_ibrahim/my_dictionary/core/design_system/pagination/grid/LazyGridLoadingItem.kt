package dev.bayan_ibrahim.my_dictionary.core.design_system.pagination.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState

fun LazyGridScope.lazyGridLoadingItem(
    loadState: LoadState,
) {
    if (loadState == LoadState.Loading) {
        item(
            span = { GridItemSpan(this.maxLineSpan) }
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        }
    }
}
