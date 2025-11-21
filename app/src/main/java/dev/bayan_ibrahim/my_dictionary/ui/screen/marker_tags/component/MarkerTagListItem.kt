package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2Action
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.MDColorPickerDialog
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CheckboxItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ImportantAction
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
                title = tag.label,
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
        MDCard2(
            footer = {
                MDCard2ActionRow {
                    MDCard2ImportantAction(
                        label = firstCapStringResource(R.string.delete_x, firstCapStringResource(R.string.tag)),
                        onClick = onRemoveTag
                    )
                    MDCard2Action(
                        label = firstCapStringResource(R.string.remove_x, firstCapStringResource(R.string.color)),
                        onClick = onRemoveMarker,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        ) {
            MDCard2CheckboxItem(
                checked = tag.passColorToChildren,
                onCheckedChange = {
                    onToggleInheritedMarkerColor(!tag.passColorToChildren)
                },
                title = firstCapStringResource(R.string.apply_to_inner_tags),
            )
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