package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGridGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.checkboxItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.ui.MDColorPickerDialog
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.DEFAULT_FRACTION

@Composable
fun MDMarkerTagListItem(
    tag: Tag,
    onChangeColor: (Color) -> Unit,
    onToggleInheritedMarkerColor: (Boolean) -> Unit,
    onRemoveMarker: () -> Unit,
    onRemoveTag: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showColorPickerDialog by remember {
        mutableStateOf(false)
    }
    // dialog:
    MDColorPickerDialog(
        showDialog = showColorPickerDialog,
        onDismissRequest = { showColorPickerDialog = false },
        onConfirm = onChangeColor,
        initialColor = tag.color ?: Color.Red
    )
    val headerTheme = tag.color?.let {
        MDCard2ListItemTheme.Custom(
            MDCard2ListItemTheme.SurfaceContainer
        ).lerp(it, DEFAULT_FRACTION)
    } ?: MDCard2ListItemTheme.SurfaceContainer

    MDCard2(
        modifier = modifier,
        headerTheme = headerTheme,
        header = {
            MDCard2ListItem(
                title = tag.value,
                leadingIcon = {
                    MDIcon(icon = MDIconsSet.WordTag)
                },
                onTrailingClick = {
                    showColorPickerDialog = true
                },
                trailingIcon = {
                    MDIcon(MDIconsSet.Colors) // TODO, icon res
                }
            )
        }
    ) {
        MDHorizontalCardGridGroup(
            modifier = Modifier.height(84.dp), // two lines
            columns = GridCells.Fixed(2),
            shape = MaterialTheme.shapes.small.copy(CornerSize(0.dp)),
        ) {
            checkboxItem(
                checked = tag.passColorToChildren,
                onClick = {
                    onToggleInheritedMarkerColor(!tag.passColorToChildren)
                },
                span = {
                    GridItemSpan(this.maxLineSpan)
                },
                title = {
                    Text(firstCapStringResource(R.string.apply_to_inner_tags))
                },
            )
            item(onClick = onRemoveTag) {
                Text(
                    text = firstCapStringResource(R.string.delete_x, firstCapStringResource(R.string.tag)),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            item(onClick = onRemoveMarker) {
                Text(
                    text = firstCapStringResource(R.string.remove_x, firstCapStringResource(R.string.color)),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun MDMarkerTagListItemPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDMarkerTagListItem(
                    tag = Tag(id = 1, value = "object/food/healthy/fruit"),
                    onChangeColor = {},
                    onToggleInheritedMarkerColor = {},
                    onRemoveMarker = {},
                    onRemoveTag = {}
                )
            }
        }
    }
}