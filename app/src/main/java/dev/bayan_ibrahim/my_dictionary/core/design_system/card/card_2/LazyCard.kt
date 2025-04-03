package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2

import androidx.annotation.IntRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LazyCard2(
    modifier: Modifier = Modifier,
    @IntRange(from = 0)
    contentCount: Int,
    overline: (@Composable () -> Unit)? = null,
    stickyOverline: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    stickyHeader: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable (i: Int) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        card2Content(
            contentCount = contentCount,
            overline = overline,
            stickyOverline = stickyOverline,
            header = header,
            stickyHeader = stickyHeader,
            footer = footer,
            content = content
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.card2Content(
    @IntRange(from = 0)
    contentCount: Int,
    overline: (@Composable () -> Unit)? = null,
    stickyOverline: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    stickyHeader: Boolean = false,
    footer: @Composable (() -> Unit)?,
    content: @Composable (i: Int) -> Unit,
) {
    if (overline != null) {
        if (stickyOverline) {
            stickyHeader {
                overline()
            }
        } else {
            item {
                overline()
            }
        }
    }
    if (header != null) {
        card2Header(item = header, stickyHeader = stickyHeader)
    }
    items(contentCount) {
        content(it)
    }
    if (footer != null) {
        card2Footer(item = footer)

    }
}

fun LazyListScope.card2Item(
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

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.card2StickyHeader(
    clipTop: Boolean = true,
    clipBottom: Boolean = true,
    clipStart: Boolean = true,
    clipEnd: Boolean = true,
    item: @Composable () -> Unit,
) {
    stickyHeader {
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

fun LazyListScope.card2Header(
    clipBottom: Boolean = false,
    clipStart: Boolean = true,
    clipEnd: Boolean = true,
    stickyHeader: Boolean = false,
    item: @Composable () -> Unit,
) {
    if (stickyHeader) {
        card2StickyHeader(
            clipTop = true,
            clipBottom = clipBottom,
            clipStart = clipStart,
            clipEnd = clipEnd,
            item = item
        )
    } else {
        card2Item(
            clipTop = true,
            clipBottom = clipBottom,
            clipStart = clipStart,
            clipEnd = clipEnd,
            item = item
        )
    }
}


fun LazyListScope.card2Footer(
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

fun cardShape(
    clipTop: Boolean = true,
    clipBottom: Boolean = true,
    clipStart: Boolean = true,
    clipEnd: Boolean = true,
    radius: Dp = MDCard2Defaults.cornerRadius,
): RoundedCornerShape {
    return RoundedCornerShape(
        topStart = if (clipTop && clipStart) radius else 0.dp,
        topEnd = if (clipTop && clipEnd) radius else 0.dp,
        bottomEnd = if (clipBottom && clipEnd) radius else 0.dp,
        bottomStart = if (clipBottom && clipStart) radius else 0.dp,
    )
}