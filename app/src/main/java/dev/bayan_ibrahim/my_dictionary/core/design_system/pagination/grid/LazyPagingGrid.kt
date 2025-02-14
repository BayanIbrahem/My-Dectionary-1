package dev.bayan_ibrahim.my_dictionary.core.design_system.pagination.grid

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapPluralsResource

fun <T : Any> LazyGridScope.lazyPagingGridItems(
    items: LazyPagingItems<T>,
    key: ((T) -> Any)? = null,
    emptyItemsPlaceHolder: @Composable () -> Unit = {
        Text(eachFirstCapPluralsResource(R.plurals.item, 0))
    },
    content: @Composable (i: Int, item: T) -> Unit,
) {
    lazyGridLoadingItem(loadState = items.loadState.prepend)
    lazyGridLoadingItem(loadState = items.loadState.refresh)
    if (items.itemCount == 0 && items.loadState.isIdle) {
        item(
            span = { GridItemSpan(this.maxLineSpan) }
        ) {
            emptyItemsPlaceHolder()
        }
    }
    lazyGridListItems(items = items, key = key, content = content)
    lazyGridLoadingItem(loadState = items.loadState.append)
}
