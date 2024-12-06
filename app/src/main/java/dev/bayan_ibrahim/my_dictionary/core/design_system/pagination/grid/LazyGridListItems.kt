package dev.bayan_ibrahim.my_dictionary.core.design_system.pagination.grid

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey

fun <T : Any> LazyGridScope.lazyGridListItems(
    items: LazyPagingItems<T>,
    key: ((T) -> Any)? = null,
    content: @Composable (i: Int, item: T) -> Unit,
) {
    if (items.loadState.isIdle) {
        items(
            count = items.itemCount,
            key = items.itemKey(key)
        ) { i ->
            items[i]?.let { item ->
                content(i, item)
            }
        }
    }
}
