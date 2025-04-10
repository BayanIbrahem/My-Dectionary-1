package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2

import androidx.annotation.IntRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.LocalClipCardListItem

@Composable
fun LazyGridCard2(
    columns: GridCells,
    modifier: Modifier = Modifier,
    @IntRange(from = 0)
    contentCount: Int,
    overline: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable (i: Int) -> Unit,
) {
    CompositionLocalProvider(
        LocalClipCardListItem provides false
    ) {
        LazyVerticalGrid(
            modifier = modifier.clip(RoundedCornerShape(MDCard2Defaults.cornerRadius)),
            columns = columns
        ) {
            card2Content(
                contentCount = contentCount,
                overline = overline,
                header = header,
                footer = footer,
                content = content,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyGridScope.card2Content(
    @IntRange(from = 0)
    contentCount: Int,
    overline: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: @Composable (() -> Unit)?,
    content: @Composable (i: Int) -> Unit,
) {
    if (overline != null) {
        item {
            overline()
        }
    }
    if (header != null) {
        card2Header(item = header)
    }
    items(contentCount) {
        content(it)
    }
    if (footer != null) {
        card2Footer(item = footer)

    }
}

fun LazyGridScope.card2Item(
    clipTop: Boolean = true,
    clipBottom: Boolean = true,
    clipStart: Boolean = true,
    clipEnd: Boolean = true,
    item: @Composable () -> Unit,
) {
    item {
        val shape by remember(clipTop, clipBottom, clipStart, clipEnd) {
            derivedStateOf {
                cardShape(
                    clipTop = clipTop,
                    clipBottom = clipBottom,
                    clipStart = clipStart,
                    clipEnd = clipEnd
                )
            }
        }
        Box(modifier = Modifier.clip(shape)) {
            item()
        }
    }
}

fun LazyGridScope.card2Header(
    clipBottom: Boolean = false,
    clipStart: Boolean = true,
    clipEnd: Boolean = true,
    item: @Composable () -> Unit,
) {
    card2Item(
        clipTop = true,
        clipBottom = clipBottom,
        clipStart = clipStart,
        clipEnd = clipEnd,
        item = item
    )
}


fun LazyGridScope.card2Footer(
    clipTop: Boolean = false,
    clipStart: Boolean = true,
    clipEnd: Boolean = true,
    item: @Composable () -> Unit,
) {
    card2Item(
        clipTop = clipTop,
        clipBottom = true,
        clipStart = clipStart,
        clipEnd = clipEnd,
        item = item
    )
}