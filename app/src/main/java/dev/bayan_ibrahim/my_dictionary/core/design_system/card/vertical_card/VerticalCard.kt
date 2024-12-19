package dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Suppress("MemberVisibilityCanBePrivate")
data object MDCardDefaults {
    val shape: CornerBasedShape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.medium

    @Composable
    fun colors(
        headerContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        headerContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        contentContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentContentColor: Color = MaterialTheme.colorScheme.onSurface,
        footerContainerColor: Color = contentContainerColor,
        footerContentColor: Color = contentContentColor,
    ): MDCardColors = MDCardColors(
        headerContainerColor = headerContainerColor,
        headerContentColor = headerContentColor,
        contentContainerColor = contentContainerColor,
        contentContentColor = contentContentColor,
        footerContainerColor = footerContainerColor,
        footerContentColor = footerContentColor
    )

    @Composable
    fun textStyles(
        headerTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
        contentTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        footerTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    ) = MDCardTextStyle(
        headerTextStyle = headerTextStyle,
        contentTextStyle = contentTextStyle,
        footerTextStyle = footerTextStyle
    )

    val headerHeight: Dp = 42.dp
    val headerPaddingValues: PaddingValues
        get() = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 4.dp)


    val contentPaddingValues: PaddingValues
        get() = PaddingValues(horizontal = 8.dp)

    val footerHeight: Dp = 48.dp
    val footerPaddingValues: PaddingValues
        get() = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)

    val headerModifier: Modifier
        get() = Modifier
            .height(headerHeight)
            .fillMaxWidth()
            .padding(headerPaddingValues)

    val contentModifier: Modifier
        get() = Modifier
            .fillMaxWidth()
            .padding(contentPaddingValues)

    val footerModifier: Modifier
        get() = Modifier
            .height(footerHeight)
            .fillMaxWidth()
            .padding(footerPaddingValues)
}

@Immutable
data class MDCardColors(
    val headerContainerColor: Color,
    val headerContentColor: Color,
    val contentContainerColor: Color,
    val contentContentColor: Color,
    val footerContainerColor: Color,
    val footerContentColor: Color,
)

@Immutable
data class MDCardTextStyle(
    val headerTextStyle: TextStyle,
    val contentTextStyle: TextStyle,
    val footerTextStyle: TextStyle,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDVerticalCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MDCardDefaults.shape,
    colors: MDCardColors = MDCardDefaults.colors(),
    textStyle: MDCardTextStyle = MDCardDefaults.textStyles(),
    headerModifier: Modifier = MDCardDefaults.headerModifier,
    footerModifier: Modifier = MDCardDefaults.footerModifier,
    contentModifier: Modifier = MDCardDefaults.contentModifier,
    headerClickable: Boolean = true,
    cardClickable: Boolean = true,
    onClickHeader: () -> Unit = {},
    onLongClickHeader: () -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    header: (@Composable BoxScope.() -> Unit)? = null,
    footer: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(shape)
            .combinedClickable(
                enabled = cardClickable,
                onLongClick = onLongClick,
                onClick = onClick,
            )
            .then(modifier),
    ) {
        header?.let {
            Surface(
                color = colors.headerContainerColor,
                contentColor = colors.headerContentColor,
                content = {
                    CompositionLocalProvider(
                        value = LocalTextStyle provides textStyle.headerTextStyle
                    ) {
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    enabled = headerClickable,
                                    onLongClick = onLongClickHeader,
                                    onClick = onClickHeader,
                                )
                                .then(headerModifier),
                            content = header,
                        )
                    }
                },
            )
        }
        Surface(
            color = colors.contentContainerColor,
            contentColor = colors.contentContentColor,
            content = {
                CompositionLocalProvider(
                    LocalTextStyle provides textStyle.contentTextStyle
                ) {
                    Box(
                        modifier = contentModifier,
                        content = content
                    )
                }

            },
        )
        footer?.let {
            Surface(
                color = colors.footerContainerColor,
                contentColor = colors.footerContentColor,
                content = {
                    CompositionLocalProvider(
                        LocalTextStyle provides textStyle.contentTextStyle,
                    ) {
                        Box(
                            modifier = footerModifier,
                            content = footer,
                        )
                    }
                }
            )
        }
    }
}


@Preview
@Composable
private fun MDCardPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDVerticalCard(
                    header = { Text("Header") },
                    footer = { Text("Footer") }
                ) { Text("Content") }
            }
        }
    }
}