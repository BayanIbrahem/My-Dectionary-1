package dev.bayan_ibrahim.my_dictionary.core.design_system.pagination.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState

fun LazyListScope.lazyListLoadingItem(
    loadState: LoadState,
) {
    if (loadState == LoadState.Loading) {
        item() {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        }
    }
}
