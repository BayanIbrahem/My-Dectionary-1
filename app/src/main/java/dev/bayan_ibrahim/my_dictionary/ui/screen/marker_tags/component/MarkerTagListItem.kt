package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGridGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.checkboxItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.core.ui.MDColorPickerDialog
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerpOnSurface
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerpSurface

@Composable
fun MDMarkerTagListItem(
    tag: ContextTag,
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
    val primaryColor = MaterialTheme.colorScheme.surfaceContainer
    val headerColorSeed by remember(primaryColor, tag.color) {
        derivedStateOf {
            tag.color ?: primaryColor
        }
    }
    MDVerticalCard(
        modifier = modifier,
        headerClickable = false,
        cardClickable = false,
        contentModifier = Modifier.fillMaxWidth(),
        colors = MDCardDefaults.colors(
            headerContainerColor = headerColorSeed.lerpSurface(),
            headerContentColor = headerColorSeed.lerpOnSurface(),
        ),
        header = {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MDIcon(icon = MDIconsSet.WordTag)
                Text(tag.value, modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        showColorPickerDialog = true
                    }
                ) {
                    MDIcon(MDIconsSet.Colors) // TODO, icon res
                }
            }
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
                    Text("Apply to inner tags") // TODO, string res
                },
            )
            item(onClick = onRemoveTag) {
                Text(
                    text = "Remove Tag", // TODO, string res
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            item(onClick = onRemoveMarker) {
                Text(
                    text = "Remove Marker color", // TODO, string res
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
                    tag = ContextTag(id = 1, value = "object/food/healthy/fruit"),
                    onChangeColor = {},
                    onToggleInheritedMarkerColor = {},
                    onRemoveMarker = {},
                    onRemoveTag = {}
                )
            }
        }
    }
}