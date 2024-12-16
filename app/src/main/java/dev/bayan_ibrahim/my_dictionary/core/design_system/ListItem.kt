package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val listColors by remember(colors, enabled) {
        derivedStateOf {
            if (enabled) {
                colors
            } else {
                ListItemColors(
                    containerColor = colors.containerColor,
                    headlineColor = colors.disabledHeadlineColor,
                    leadingIconColor = colors.disabledLeadingIconColor,
                    overlineColor = colors.disabledLeadingIconColor,
                    supportingTextColor = colors.disabledLeadingIconColor,
                    trailingIconColor = colors.disabledTrailingIconColor,
                    disabledHeadlineColor = colors.disabledHeadlineColor,
                    disabledLeadingIconColor = colors.disabledLeadingIconColor,
                    disabledTrailingIconColor = colors.disabledTrailingIconColor
                )
            }
        }
    }
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = listColors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation
    )
}

@Preview
@Composable
private fun MDListItemPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDListItem(
                    headlineContent = {
                        Text("Headline")
                    }
                )
            }
        }
    }
}