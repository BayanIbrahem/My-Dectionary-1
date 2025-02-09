package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum

@Composable
fun <E> IconSegmentedButton(
    selected: E? = null,
    allItems: Collection<E>,
    onSelectItem: (E?) -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) where E : IconedEnum, E : Enum<E> {
    val nonSelectedItems by remember(selected, allItems) {
        derivedStateOf {
            if (selected == null) {
                allItems
            } else {
                allItems - selected
            }
        }
    }
    LazyRow(
        modifier = modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, horizontalAlignment)
    ) {
        if (selected != null) {
            item(
                key = selected
            ) {
                IconButton(
                    modifier = Modifier.animateItem(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    onClick = {
                        onSelectItem(null)
                    }
                ) {
                    MDIcon(selected.icon)
                }
            }
            item {
                VerticalDivider(
                    modifier = Modifier
                        .height(36.dp)
                        .animateItem()
                )
            }
        }
        items(
            items = nonSelectedItems.toList(),
            key = { it.ordinal }
        ) {
            FilledIconButton(
                modifier = Modifier.animateItem(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = { onSelectItem(it) }
            ) {
                MDIcon(it.icon)
            }
        }
    }
}