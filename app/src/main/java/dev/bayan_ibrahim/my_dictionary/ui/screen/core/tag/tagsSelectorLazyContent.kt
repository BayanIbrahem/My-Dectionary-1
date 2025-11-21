package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDropdown
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDSearchField
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.MDColorPickerDialog
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDDropdownListItem
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.tagsSelectorLazyContent(
    uiState: MDTagsSelectorUiState,
    uiActions: MDTagsSelectorUiActions,
) {
    stickyHeader {
        MDSearchField(
            placeholder = firstCapStringResource(R.string.search),
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = {
                uiActions.onSearchQueryChange(it)
            }
        )
    }
    itemsIndexed(
        items = uiState.visibleTagsList,
        key = { i, it -> it.id }
    ) { i, tag ->
        val startPadding by animateDpAsState(uiState.tagsTree.level(tag.id).times(24).dp)
        val parent by remember(tag.id, uiState.tagsTree) {
            derivedStateOf {
                uiState.tagsTree.parent(tag.id)
            }
        }
        val parentsOptions by remember(uiState.tagsTree, tag.id) {
            derivedStateOf {
                uiState.tagsTree.run {
                    tags - predecessors(tag.id)
                }.toSet()
            }
        }
        TagListItem(
            modifier = Modifier
                .animateItem()
                .padding(start = startPadding),
            tag = tag,
            isEditMode = uiState.currentEditTagId == tag.id,
            parent = parent,
            parentsOptions = parentsOptions,
            selected = tag.id in uiState.selectedTagsIds,
            selectEnabled = true,
            onToggleSelect = {
                uiActions.onToggleSelectTag(tag, it)
            },
            onDelete = {
                uiActions.onDeleteTag(tag)
            },
            onConfirmEditLabel = { newLabel ->
                uiActions.onConfirmEditTagLabel(tag, newLabel)
            },
            onCancelEditLabel = {
                uiActions.onCancelEditTagLabel()
            },
            onDeleteSubtree = {
                uiActions.onDeleteTagSubtree(tag)
            },
            onChangeParent = { parent ->
                uiActions.onChangeParent(tag = tag, parent = parent)
            },
            onEditLabel = {
                uiActions.onRequestEditTagLabel(tag)
            },
            onEditColor = { color, pass ->
                uiActions.onEditColor(id = tag.id, color = color, bool = pass)
            },
            onAddChild = {
                uiActions.onAddChild(parent = tag)
            },
        )
    }
}

@Composable
private fun TagListItem(
    tag: Tag,
    parent: Tag?,
    parentsOptions: Set<Tag>,
    onToggleSelect: (Boolean) -> Unit,
    onAddChild: () -> Unit,
    onEditLabel: () -> Unit,
    onConfirmEditLabel: (newLabel: String) -> Unit,
    onCancelEditLabel: () -> Unit,
    onEditColor: (newColor: Color?, passColor: Boolean) -> Unit,
    onDelete: () -> Unit,
    onDeleteSubtree: () -> Unit,
    onChangeParent: (parent: Tag?) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    selectEnabled: Boolean = false,
    addEnabled: Boolean = false,
    deleteEnabled: Boolean = false,
    editEnabled: Boolean = false,
    deleteSubtreeEnabled: Boolean = false,
) {
    val hasDropdownMenu by remember(editEnabled, deleteEnabled, deleteSubtreeEnabled, addEnabled) {
        derivedStateOf {
            editEnabled || deleteEnabled || deleteSubtreeEnabled || addEnabled
        }
    }
    var showDropdownMenu by remember {
        mutableStateOf(false)
    }

    var showColorPickerDialog by remember {
        mutableStateOf(false)
    }
    // dialog:
    MDColorPickerDialog(
        showDialog = showColorPickerDialog,
        onDismissRequest = { showColorPickerDialog = false },
        onConfirm = { color, pass ->
            onEditColor(color, pass)
        },
        initialColor = tag.color ?: Color.Red,
        initPassColor = tag.passColor
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AnimatedVisibility(
            visible = selectEnabled,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Checkbox(
                checked = selected,
                enabled = selectEnabled,
                onCheckedChange = onToggleSelect
            )
        }
        MDCard2ListItem(
            title = {
                if (isEditMode) {
                    val state by remember {
                        derivedStateOf {
                            TextFieldState()
                        }
                    }
                    val focusRequester by remember {
                        derivedStateOf { FocusRequester() }
                    }
                    LaunchedEffect(true) {
                        /// request focus for the text field so when we add an item we add an item we ensure that
                        // this item has focus
                        focusRequester.requestFocus()
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BasicTextField(
                            state = state,
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (!it.isFocused) {
                                        onCancelEditLabel()
                                    }
                                },
                        )
                        IconButton(
                            onClick = onCancelEditLabel
                        ) {
                            MDIcon(MDIconsSet.Close)
                        }
                        val enableCheck by remember(state.text, tag.label) {
                            derivedStateOf {
                                state.text.isNotEmpty() && tag.label.tagMatchNormalize == state.text.toString().tagMatchNormalize
                            }
                        }
                        IconButton(
                            onClick = {
                                if (enableCheck) {
                                    onConfirmEditLabel(state.text.toString())
                                }
                            },
                            enabled = enableCheck,
                        ) {
                            MDIcon(MDIconsSet.Check)
                        }
                    }
                } else {
                    Text(tag.label)
                }
            },
            onClick = {
                if (!isEditMode) {
                    showDropdownMenu = true
                }
            },
            leading = if (tag.id == MDTagsSelectorUiState.NEW_TAG_ID) {
                {
                    Text(
                        text = stringResource(R.string._new),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                null
            }
        )
        MDDropdownListItem(
            enabled = parentsOptions.isNotEmpty() && editEnabled,
            dropdownContent = {
                MDCard2ListItem(
                    // TODO, string res
                    title = {
                        Text(
                            text = firstCapStringResource(R.string.remove_x, "parent"),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        onChangeParent(null)
                    },
                )
                parentsOptions.forEach { parentOption ->
                    MDCard2ListItem(
                        title = parentOption.label,
                        onClick = {
                            onChangeParent(parentOption)
                        },
                        trailingIcon = if (parentOption.id == parent?.id) {
                            {
                                MDIcon(MDIconsSet.Check)
                            }
                        } else null,
                    )
                }
            },
            title = {
                if (parent != null) {
                    Text(parent.label)
                } else {
                    Text(
                        text = if (editEnabled) {
                            // TODO, string res
                            "Select Parent"
                        } else {
                            // TODO, string res
                            "No parent"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
        MDDropdown(
            expanded = showDropdownMenu && hasDropdownMenu,
            onDismissRequest = {
                showDropdownMenu = false
            },
        ) {
            if (deleteSubtreeEnabled) {
                MDCard2ListItem(
                    title = firstCapStringResource(
                        R.string.delete_x,
                        // TODO, string res
                        "Delete Subtree",
                    ),
                    onClick = onDeleteSubtree,
                )
            }
            if (deleteEnabled) {
                MDCard2ListItem(
                    title = firstCapStringResource(R.string.delete),
                    onClick = onDelete
                )
            }
            if (addEnabled) {
                MDCard2ListItem(
                    title = firstCapStringResource(R.string.add),
                    onClick = onAddChild
                )
            }
            if (editEnabled) {
                MDCard2ListItem(
                    title = firstCapStringResource(R.string.update),
                    onClick = onEditLabel,
                )

                MDCard2ListItem(
                    title = firstCapStringResource(R.string.color),
                    onClick = {
                        showColorPickerDialog = true
                    },
                )
            }
        }
    }
}
