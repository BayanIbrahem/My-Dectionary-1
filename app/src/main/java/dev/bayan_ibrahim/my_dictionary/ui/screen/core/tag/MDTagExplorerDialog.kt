package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDialogDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDSearchDialogInputField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagSegmentSeparator
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.asTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.depth
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.parentAtLevel
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDTagExplorerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    state: MDTagsSelectorUiState,
    actions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    // ui
    tagTrailingIcon: (@Composable (Tag) -> Unit)? = { tag ->
        tag.wordsCount?.takeIf {
            it > 0
        }?.let {
            Text("x$it")
        }
    },
    tagLeadingIcon: @Composable (tag: Tag, isLeaf: Boolean) -> Unit = { _, _ ->
        MDIcon(MDIconsSet.WordTag) // TODO, icons tag
    },
    primaryActionLabel: String = "Select", // TODO, string res
    /**
     * callback when click a list item, passing current tag, and destination tag and if the destination
     * tag is leaf or not
     */
    // search:
    searchFilterHidesTagsOnly: Boolean = false,
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
    MDBasicDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .sizeIn(maxWidth = 300.dp),
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismissRequest,
                primaryActionLabel = primaryActionLabel,
                primaryClickEnabled = state.isSelectEnabled,
                onPrimaryClick = {
                    actions.onSelectCurrentTag()
                }
            )
        },
        contentModifier = MDCardDefaults.contentModifier.padding(vertical = 8.dp),
        headerModifier = Modifier
            .heightIn(min = MDCardDefaults.headerHeight)
            .fillMaxWidth()
            .padding(MDCardDefaults.headerPaddingValues),
        title = {
            DialogHeader(
                state = state,
                actions = actions,
                isAddNewTagInProgress = isAddNewTagInProgress,
                allowAddTags = allowAddTags,
                onAddNewTag = {
                    isAddNewTagInProgress = true
                }
            )
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDHorizontalCardGroup {
                state.currentTagsTree.nextLevel.forEach { (segment, subTree) ->
                    val (isVisible, isEnabled) = subTree.tag?.let { tag ->
                        val matchSearchQuery = tag.segments.last().tagMatchNormalize.startsWith(searchQuery.tagMatchNormalize)
                        val isVisible = searchFilterHidesTagsOnly || matchSearchQuery
                        val isEnabled = isVisible && matchSearchQuery && subTree.tag !in state.disabledTags
                        Pair(isVisible, isEnabled)
                    } ?: Pair(true, true)
                    if (isVisible) {
                        item(
                            enabled = isEnabled,
                            leadingIcon = {
                                subTree.tag?.let {
                                    tagLeadingIcon(it, subTree.isLeaf)
                                }
                            },
                            trailingIcon = tagTrailingIcon?.let { composable ->
                                subTree.tag?.let { tag ->
                                    { composable(tag) }
                                }
                            },
                            onClick = {
                                subTree.tag?.let { actions.onClickTag(it) }
                            },
                            onLongClick = if (allowRemoveTags) {
                                {
                                    deleteConfirmTag = subTree.tag
                                }
                            } else null
                        ) {
                            Text(segment)
                        }
                    }
                }
                if (state.currentTagsTree.isLeaf) {
                    item {
                        Text("No Inner tags for current")
                    }
                }
            }
            if (isAddNewTagInProgress) {
                var isFieldFocused by remember {
                    mutableStateOf(false)
                }
                MDBasicTextField(
                    colors = MDTextFieldDefaults.colors(
                        unfocusedContainerColor = MDCardDefaults.colors().contentContainerColor
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
    MDAlertDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        textStyle = MDCardDefaults.textStyles(headerTextStyle = MaterialTheme.typography.titleLarge),
        headerModifier = Modifier.padding(8.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Delete Context Tag",
                    modifier = Modifier.weight(1f),
                )// TODO, string res
            }
        },
        modifier = modifier,
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismissRequest,
                onPrimaryClick = onConfirmDelete,
                onSecondaryClick = onDismissRequest,
                primaryActionLabel = "Delete",
                colors = MDDialogDefaults.colors(
                    primaryActionColor = MaterialTheme.colorScheme.error,
                ),
                dismissOnPrimaryClick = false,
                dismissOnSecondaryClick = false,
            )
        },
    ) {
        Text(
            text = "Delete ${selectedTag?.value ?: ""} this action can not be undone",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )// TODO, string res
    }
}

@Composable
private fun DialogHeader(
    state: MDTagsSelectorUiState,
    actions: MDTagsSelectorUiActions,
    isAddNewTagInProgress: Boolean,
    allowAddTags: Boolean,
    onAddNewTag: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = !isCurrentRoot,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
            IconButton(
                enabled = !isCurrentRoot,
                onClick = actions::onNavigateUp
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back), // TODO, icon res
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .animateContentSize { initialValue, targetValue -> },
        ) {
            Text("Tags tree", style = MaterialTheme.typography.titleMedium)
            MDSearchDialogInputField(
                modifier = Modifier.fillMaxWidth(),
                searchQuery = state.searchQuery,
                onSearchQueryChange = actions::onSearchQueryChange,
                label = "",
                placeholder = randomTagLastSegment?.let { "e.g $it" /*TODO, string res */ } ?: ""
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
        if (allowAddTags) {
            IconButton(
                enabled = !isAddNewTagInProgress,
                onClick = onAddNewTag
            ) {
                MDIcon(MDIconsSet.Add) // TODO, icon res
            }
        }
    }
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
                        viewModel.getUiActions(object : MDTagsSelectorNavigationUiActions{})
                    }
                }
                MDTagExplorerDialog(
                    showDialog = true,
                    onDismissRequest = {

                    },
                    state = state,
                    actions = actions,
                    tagTrailingIcon = {
                        MDIcon(MDIconsSet.Delete)
                    },
                )
            }
        }
    }
}