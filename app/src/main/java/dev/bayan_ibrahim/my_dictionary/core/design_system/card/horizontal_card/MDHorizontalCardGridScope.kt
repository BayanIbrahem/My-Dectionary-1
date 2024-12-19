package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScope
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScopeImpl
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScopeProvider
import dev.bayan_ibrahim.my_dictionary.core.design_system.rememberStateOfItems

data class MDHorizontalCardGridScopeItem(
    override val modifier: Modifier = Modifier,
    override val enabled: Boolean = true,
    override val onClick: (() -> Unit)? = null,
    override val onLongClick: (() -> Unit)? = null,
    override val colors: MDHorizontalCardColors? = null,
    override val styles: MDHorizontalCardStyles? = null,
    val span: (LazyGridItemSpanScope.(currentIndex: Int) -> GridItemSpan)? = null,
    override val leadingIcon: @Composable (() -> Unit)? = null,
    override val trailingIcon: @Composable (() -> Unit)? = null,
    override val subtitle: (@Composable () -> Unit)? = null,
    override val title: @Composable () -> Unit,
) : MDHorizontalCardScopeItem(
    modifier = modifier,
    enabled = enabled,
    onClick = onClick,
    onLongClick = onLongClick,
    colors = colors,
    styles = styles,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    subtitle = subtitle,
    title = title
)

typealias MDHorizontalCardGridScope = MDUiScope<MDHorizontalCardGridScopeItem>

class MDHorizontalCardGridScopeImpl : MDUiScopeImpl<MDHorizontalCardGridScopeItem>() {
    override val items: MutableVector<MDHorizontalCardGridScopeItem> = mutableVectorOf()
}

@Composable
fun rememberHorizontalCardGridStateOfItems(
    items: MDUiScope<MDHorizontalCardGridScopeItem>.() -> Unit,
): State<MDUiScopeProvider<MDHorizontalCardGridScopeItem>> = rememberStateOfItems(
    impl = {
        MDHorizontalCardGridScopeImpl()
    },
    content = items
)

