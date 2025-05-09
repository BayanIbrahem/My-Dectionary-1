package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDSearchDialogInputField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CancelAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ConfirmAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2SelectableItem
import dev.bayan_ibrahim.my_dictionary.core.ui.dialog.MDDeleteConfirmDialog
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagSegmentSeparator
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.asTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.depth
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.parentAtLevel
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerp

@Composable
fun MDTagExplorerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    state: MDTagsSelectorUiState,
    actions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    tagLeadingIcon: @Composable (tag: Tag, isLeaf: Boolean) -> Unit = { tag, isLeaf ->
        val fill = tag.color?.lerp(MaterialTheme.colorScheme.surfaceContainer)
        val tint = tag.color?.lerp(MaterialTheme.colorScheme.onSurface) ?: LocalContentColor.current
        Log.i("Tag", tag.toString())
        MDIcon(
            icon = MDIconsSet.WordTag,
            fill = fill,
            outline = tag.color == null,
            tint = tint,
        ) // TODO, icons tag
    },
    primaryActionLabel: String = firstCapStringResource(R.string.select),
    /**
     * callback when click a list item, passing current tag, and destination tag and if the destination
     * tag is leaf or not
     */
    // search:
    searchFilterHidesTagsOnly: Boolean = true,
    // permissions:
    /**
     * toggle permission to create new tags to database
     */
    allowAddTags: Boolean = true,
    /**
     * toggle permissions to remove tags to database (permanently)
     */
    allowRemoveTags: Boolean = true,
) {
    var isAddNewTagInProgress by remember {
        mutableStateOf(false)
    }
    var newTagSegmentText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(isAddNewTagInProgress) {
        newTagSegmentText = ""
    }

    var searchQuery by remember {
        mutableStateOf("")
    }
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            MDCard2(
                modifier = modifier,
                cardModifier = Modifier.width(IntrinsicSize.Max),
                header = {
                    DialogHeader(
                        state = state,
                        actions = actions,
                    )
                },
                footer = {
                    MDCard2ActionRow {
                        MDCard2ConfirmAction(
                            label = primaryActionLabel,
                            onClick = {
                                actions.onSelectCurrentTag()
                                onDismissRequest()
                            },
                            enabled = state.isSelectEnabled,
                        )
                        if (allowAddTags) {
                            MDCard2ConfirmAction(
                                label = firstCapStringResource(R.string.add),
                                // TODO, make the same button to save the new tag
                                enabled = !isAddNewTagInProgress,
                                onClick = {
                                    isAddNewTagInProgress = true
                                },
                            )
                        }
                        MDCard2CancelAction(onClick = onDismissRequest)
                    }
                }
            ) {
                var deleteConfirmTag: Tag? by remember {
                    mutableStateOf(null)
                }
                DeleteTagConfirmDialog(
                    selectedTag = deleteConfirmTag,
                    onDismissRequest = { deleteConfirmTag = null },
                    onConfirm = actions::onDeleteTag
                )
                Column(
                    modifier = Modifier
                        .heightIn(max = 250.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    state.currentTagsTree.nextLevel.forEach { (segment, subTree) ->
                        val isVisibleWithEnabled by remember(subTree.tag) {
                            derivedStateOf {
                                subTree.tag?.let { tag ->
                                    val matchSearchQuery = tag.segments.last().tagMatchNormalize.startsWith(searchQuery.tagMatchNormalize)
                                    val isVisible = !searchFilterHidesTagsOnly || matchSearchQuery
                                    val isEnabled = isVisible && matchSearchQuery && subTree.tag !in state.disabledTags
                                    Pair(isVisible, isEnabled)
                                } ?: Pair(true, true)
                            }
                        }
                        if (isVisibleWithEnabled.first) {
                            MDCard2SelectableItem(
                                checked = isVisibleWithEnabled.second,
                                leading = {
                                    subTree.tag?.let {
                                        tagLeadingIcon(it, subTree.isLeaf)
                                    }
                                },
                                trailing = if (allowRemoveTags) {
                                    {
                                        IconButton(
                                            onClick = {
                                                deleteConfirmTag = subTree.tag
                                            }
                                        ) {
                                            MDIcon(MDIconsSet.Delete)
                                        }
                                    }
                                } else null,
                                onClick = if (isVisibleWithEnabled.second) {
                                    {
                                        subTree.tag?.let { actions.onClickTag(it) }
                                    }
                                } else null,
                                theme = MDCard2ListItemTheme.SurfaceDisabled,
                                checkedTheme = MDCard2ListItemTheme.SurfaceContainer,
                                title = segment,
                            )
                        }
                    }
                    if (state.currentTagsTree.isLeaf) {
                        // TODO, string res
                        MDCard2ListItem("No Inner tags for current")
                    }
                }
                if (isAddNewTagInProgress) {
                    var isFieldFocused by remember {
                        mutableStateOf(false)
                    }
                    MDBasicTextField(
                        colors = MDTextFieldDefaults.colors(
                            unfocusedContainerColor = MDCard2ListItemTheme.SurfaceContainer.containerColor,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                isFieldFocused = it.isFocused
                            },
                        value = newTagSegmentText,
                        placeholder = "new Tag",
                        onValueChange = {
                            newTagSegmentText = it.split(TagSegmentSeparator).joinToString("")
                        },
                        onKeyboardAction = {
                            // cancel
                            actions.onAddNewTag(newTagSegmentText)
                            newTagSegmentText = ""
                            isAddNewTagInProgress = false
                        },
                        trailingIcons = {
                            if (isFieldFocused) {
                                IconButton(
                                    onClick = {
                                        isAddNewTagInProgress = false
                                    }
                                ) {
                                    MDIcon(MDIconsSet.Close)
                                }

                                if (newTagSegmentText.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            actions.onAddNewTag(newTagSegmentText)
                                            newTagSegmentText = ""
                                            isAddNewTagInProgress = false
                                        }
                                    ) {
                                        MDIcon(MDIconsSet.Check)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteTagConfirmDialog(
    selectedTag: Tag?,
    onDismissRequest: () -> Unit,
    onConfirm: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showDialog by remember(selectedTag) {
        derivedStateOf {
            selectedTag != null
        }
    }
    val onConfirmDelete: () -> Unit by remember(selectedTag, onConfirm) {
        derivedStateOf {
            selectedTag?.let {
                {
                    onConfirm(it)
                    onDismissRequest()
                }
            } ?: {}
        }
    }
    MDDeleteConfirmDialog(
        modifier = modifier,
        showDialog = showDialog,
        onConfirm = onConfirmDelete,
        onCancel = onDismissRequest,
        title = firstCapStringResource(R.string.delete_x, firstCapStringResource(R.string.tag)),
        confirmDeleteMessage = firstCapStringResource(
            R.string.delete_x,
            "${selectedTag?.value ?: ""}, ${stringResource(R.string.permanent_delete_warning)}"
        ),
    )
}

@Composable
private fun DialogHeader(
    state: MDTagsSelectorUiState,
    actions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
) {
    // TODO, improve dialog
    val randomTagLastSegment by remember {
        derivedStateOf {
            state.currentTagsTree.nextLevel.values.randomOrNull()?.tag?.segments?.lastOrNull()
        }
    }
    val isCurrentRoot by remember(state.currentTagsTree) {
        derivedStateOf {
            state.currentTagsTree.isRoot
        }
    }
    MDCard2ListItem(
        modifier = modifier,
        leading = if (isCurrentRoot) null else {
            {
                IconButton(
                    onClick = actions::onNavigateUp
                ) {
                    Icon(
                        modifier = Modifier.size(MDCard2ListItemDefaults.leadingIconSize),
                        painter = painterResource(R.drawable.arrow_back), // TODO, icon res
                        contentDescription = null
                    )
                }
            }
        },
        title = {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(vertical = 8.dp)
                    .animateContentSize { initialValue, targetValue -> },
            ) {
                Text(
                    text = firstCapStringResource(R.string.tags_tree),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp),
                )
                MDSearchDialogInputField(
                    modifier = Modifier.fillMaxWidth(),
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = actions::onSearchQueryChange,
//                    label = "",
                    placeholder = randomTagLastSegment?.let { stringResource(R.string.eg_x, it) } ?: ""
                )
                state.currentTagsTree.tag?.let {
                    MDTagPath(
                        tag = it,
                        onClickSegment = { i, s ->
                            val parentTag = it.parentAtLevel(i.inc())
                            actions.onClickTag(parentTag)
                        }
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MDTagPath(
    tag: Tag,
    onClickSegment: (index: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
    levelsSeparator: String = ">",
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    FlowRow(
        modifier = modifier
    ) {
        val lastIndex = tag.depth.dec()
        tag.segments.forEachIndexed { i, segment ->
            Text(
                text = segment + if (i < lastIndex) levelsSeparator else "",
                modifier = Modifier.clickable {
                    onClickSegment(i, segment)
                },
                style = style,
            )
        }
    }
}

@Preview
@Composable
private fun MDTagPathPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                MDTagPath(
                    tag = Tag("object", "food", "vegetables"),
                    onClickSegment = { _, _ ->

                    }
                )

            }
        }
    }
}

@Preview
@Composable
private fun MDTagSelectorDialogPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val tree by remember {
                    derivedStateOf {
                        listOf(
                            Tag("object", "food", "fruit"),
                            Tag("object", "food", "vegetables"),
                            Tag("object", "food", "healthy"),
                            Tag("object", "device"),
                            Tag("language", "en"),
                        ).asTree()
                    }
                }
                val viewModel: MDTagsSelectorViewModel = hiltViewModel()
                val state = viewModel.uiState
                val actions by remember(state) {
                    derivedStateOf {
                        viewModel.getUiActions(object : MDTagsSelectorNavigationUiActions {})
                    }
                }
                MDTagExplorerDialog(
                    showDialog = true,
                    onDismissRequest = {

                    },
                    state = state,
                    actions = actions,
                )
            }
        }
    }
}