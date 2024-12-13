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

data class MDHorizontalCardScopeItem(
    val modifier: Modifier = Modifier,
    val enabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val colors: MDHorizontalCardColors? = null,
    val styles: MDHorizontalCardStyles? = null,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val trailingIcon: @Composable (() -> Unit)? = null,
    val subtitle: (@Composable () -> Unit)? = null,
    val title: @Composable () -> Unit,
) : MDUiScopeItem

typealias MDHorizontalCardScope = MDUiScope<MDHorizontalCardScopeItem>

class MDHorizontalCardScopeImpl : MDUiScopeImpl<MDHorizontalCardScopeItem>() {
    override val items: MutableVector<MDHorizontalCardScopeItem> = mutableVectorOf()
}

@Composable
fun rememberHorizontalCardStateOfItems(
    items: MDUiScope<MDHorizontalCardScopeItem>.() -> Unit,
): State<MDUiScopeProvider<MDHorizontalCardScopeItem>> = rememberStateOfItems(
    impl = MDHorizontalCardScopeImpl(),
    content = items
)

