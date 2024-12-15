package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * object data of the item
 */
interface MDUiScopeItem

/**
 * marker interface for any scope we use
 */
interface MDUiScope<Item : MDUiScopeItem> {
    fun item(item: Item)
    fun items(items: Collection<Item>)
    fun items(count: Int, builder: (Int) -> Item) = items(List(count, builder))
}

interface MDUiScopeProvider<Item : MDUiScopeItem> {
    val itemsCount: Int
        get() = items.size
    val items: MutableVector<Item>
}

abstract class MDUiScopeImpl<Item : MDUiScopeItem> : MDUiScope<Item>, MDUiScopeProvider<Item> {
    override fun item(item: Item) {
        items.add(item)
    }

    override fun items(items: Collection<Item>) {
        this.items.addAll(items)
    }
}


@Composable
fun <Item : MDUiScopeItem> rememberStateOfItems(
    impl: () -> MDUiScopeImpl<Item>,
    content: MDUiScope<Item>.() -> Unit,
): State<MDUiScopeProvider<Item>> {
    val latestContent = rememberUpdatedState(content)
    return remember() {
        derivedStateOf {
            impl().apply(latestContent.value)
        }
    }
}
