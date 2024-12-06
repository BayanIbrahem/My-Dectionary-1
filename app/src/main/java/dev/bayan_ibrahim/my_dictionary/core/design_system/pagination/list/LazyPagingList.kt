package dev.bayan_ibrahim.my_dictionary.core.design_system.pagination.list

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyListScope.lazyPagingListItems(
    items: LazyPagingItems<T>,
    key: ((T) -> Any)? = null,
    emptyItemsPlaceHolder: @Composable () -> Unit = {
        Text("No Items") // TODO, string res
    },
    content: @Composable (i: Int, item: T) -> Unit,
) {
    lazyListLoadingItem(loadState = items.loadState.prepend)
    lazyListLoadingItem(loadState = items.loadState.refresh)
    if (items.itemCount == 0 && items.loadState.isIdle) {
        item {
            emptyItemsPlaceHolder()
        }
    }
    lazyListListItems(items = items, key = key, content = content)
    lazyListLoadingItem(loadState = items.loadState.append)
}
