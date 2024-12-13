package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.core.ui.UnavailableComponentHint
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation

fun LazyListScope.wordDetailsTextFieldList(
    items: Map<Long, String>,
    onItemValueChange: (Long, String) -> Unit,
    leadingIcon: ImageVector,
    groupLabel: String,
    itemModifier: Modifier = Modifier,
    itemsPlaceholder: String = "Add (leave blank to delete)",// TODO, string res
    lastItemPlaceholder: String = "Add new",// TODO, string res
    onGroupFocusChanged: (Long?) -> Unit = {},
) {
    val focusState = mutableStateMapOf<Long, Boolean>()
    itemsIndexed(
        items = items.toList(),
        key = { _, (id, _) -> id }
    ) { i, (id, value) ->
        val onValueChange by remember {
            derivedStateOf {
                { value: String ->
                    onItemValueChange(id, value)
                }
            }
        }
        val isFirst = i == 0
        val isLast = i.inc() == items.count()
        val label = if (isFirst) groupLabel else ""
        val placeholder = if (isLast) lastItemPlaceholder else itemsPlaceholder
        MDWordFieldTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = leadingIcon,
            modifier = itemModifier
                .fillMaxWidth()
                .animateItem(),
            index = i.inc(),
            label = label,
            showLabelOnEditMode = isFirst,
            hasBottomHorizontalDivider = isLast,
            placeholder = placeholder,
            onFocusEvent = {
                focusState[id] = it.isFocused
                onGroupFocusChanged(focusState.firstFocusedItem())
            },
            onKeyboardAction = {
                onGroupFocusChanged(focusState.firstFocusedItem())
            }
        )
    }
}

fun LazyListScope.wordDetailsRelatedWordsTextFieldsList(
    items: Map<Long, Pair<WordTypeTagRelation, String>>,
    onItemValueChange: (Long, String) -> Unit,
    typeRelations: List<WordTypeTagRelation>?,
    onSelectRelation: (Long, WordTypeTagRelation) -> Unit,
    leadingIcon: ImageVector,
    groupLabel: String,
    itemModifier: Modifier = Modifier,
    relationPlaceholder: String = "Relation",// TODO, string res
    valuePlaceholder: String = "Add dev.bayan_ibrahim.my_dictionary.core.design_system.group.item (leave blank to delete)",// TODO, string res
    lastValuePlaceholder: String = "Add new dev.bayan_ibrahim.my_dictionary.core.design_system.group.item",// TODO, string res
    onGroupFocusChanged: (Long?) -> Unit = {},
) {
    val focusState = mutableStateMapOf<Long, Boolean>()
    if (typeRelations == null) { // no selected type
        item {
            UnavailableComponentHint("No relations, You should select word type tag before add related words") // TODO, string res
        }

    } else if (typeRelations.isEmpty()) { // selected type but has no relations
        item {
            UnavailableComponentHint("No relations, current word type doesn't have any relations") // TODO, string res
        }
    } else {
        itemsIndexed(
            items = items.toList(),
            //        key = { _, (id, _) -> id }
        ) { i, (id, value) ->
            val onValueChange by remember {
                derivedStateOf {
                    { value: String ->
                        onItemValueChange(id, value)
                    }
                }
            }
            val isFirst = i == 0
            val isLast = i.inc() == items.count()
            val label = if (isFirst) groupLabel else ""
            val placeholder = if (isLast) lastValuePlaceholder else valuePlaceholder
            Row(
                modifier = itemModifier.animateItem(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                MDWordFieldTextField(
                    value = value.first,
                    suggestions = typeRelations,
                    onValueChange = {},
                    fieldReadOnly = true,
                    allowCancelSelection = false,
                    onSelectSuggestion = { i, relation ->
                        relation?.let {
                            onSelectRelation(id, relation)
                        }
                    },
                    suggestionTitle = {
                        this.label
                    },
                    leadingIcon = leadingIcon,
                    label = label,
                    modifier = Modifier.weight(1f),
                    showLabelOnPreviewMode = isFirst,
                    hasBottomHorizontalDivider = isLast,
                    placeholder = relationPlaceholder,
                    onFocusEvent = {
                        focusState[id] = it.isFocused
                        onGroupFocusChanged(focusState.firstFocusedItem())
                    },
                    onKeyboardAction = {
                        onGroupFocusChanged(focusState.firstFocusedItem())
                    },
                )
                MDWordFieldTextField(
                    value = value.second,
                    onValueChange = onValueChange,
                    leadingIcon = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    hasBottomHorizontalDivider = isLast,
                    placeholder = placeholder,
                    onFocusEvent = {
                        focusState[id] = it.isFocused
                        onGroupFocusChanged(focusState.firstFocusedItem())
                    },
                    onKeyboardAction = {
                        onGroupFocusChanged(focusState.firstFocusedItem())
                    },
                    index = i.inc(),
                )
            }
        }
    }
}

private fun SnapshotStateMap<Long, Boolean>.firstFocusedItem() = entries.firstOrNull { fieldFocus ->
    fieldFocus.value
}?.key