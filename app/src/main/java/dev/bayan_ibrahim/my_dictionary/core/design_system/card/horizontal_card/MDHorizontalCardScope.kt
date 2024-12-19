package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScope
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScopeImpl
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScopeItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScopeProvider
import dev.bayan_ibrahim.my_dictionary.core.design_system.rememberStateOfItems

open class MDHorizontalCardScopeItem(
    open val modifier: Modifier = Modifier,
    open val enabled: Boolean = true,
    open val onClick: (() -> Unit)? = null,
    open val onLongClick: (() -> Unit)? = null,
    open val colors: MDHorizontalCardColors? = null,
    open val styles: MDHorizontalCardStyles? = null,
    open val leadingIcon: @Composable (() -> Unit)? = null,
    open val trailingIcon: @Composable (() -> Unit)? = null,
    open val subtitle: (@Composable () -> Unit)? = null,
    open val title: @Composable () -> Unit,
) : MDUiScopeItem

typealias MDHorizontalCardScope = MDUiScope<MDHorizontalCardScopeItem>

class MDHorizontalCardScopeImpl : MDUiScopeImpl<MDHorizontalCardScopeItem>() {
    override val items: MutableVector<MDHorizontalCardScopeItem> = mutableVectorOf()
}

@Composable
fun rememberHorizontalCardStateOfItems(
    items: MDUiScope<MDHorizontalCardScopeItem>.() -> Unit,
): State<MDUiScopeProvider<MDHorizontalCardScopeItem>> = rememberStateOfItems(
    impl = {
        MDHorizontalCardScopeImpl()
    },
    content = items
)

