package dev.bayan_ibrahim.my_dictionary.core.ui

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
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDSearchDialogInputField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagSegmentSeparator
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.asTree
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDContextTagExplorerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    tagsTree: ContextTagsTree,
    onSelect: (currentTag: ContextTag, isLeaf: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    /**
     * callback to enable/disable tags but keep viewing all, if the item is not visible according to
     * [viewFilter] then this callback would not be called
     * if the tag doesn't match search query and [searchFilterHidesTagsOnly] is true, then this
     * callback would not be called
     */
    enableFilter: (ContextTag) -> Boolean = { true },
    /**
     * callback to view/hide tags
     * if the tag doesn't match search query and [searchFilterHidesTagsOnly] is false, then this
     * callback would not be called
     */
    viewFilter: (ContextTag) -> Boolean = { true },
    /**
     * if true then tags that doesn't match search query will still be visible but disabled even
     * it its result is true in tags filter
     */
    searchFilterHidesTagsOnly: Boolean = false,
    // ui
    tagTrailingIcon: (@Composable (ContextTag) -> Unit)? = { tag ->
        tag.wordsCount?.takeIf {
            it > 0
        }?.let {
            Text("x$it")
        }
    },
    tagLeadingIcon: @Composable (tag: ContextTag, isLeaf: Boolean) -> Unit = { _, _ ->
        MDIcon(MDIconsSet.WordTag) // TODO, icons tag
    },
    primaryActionLabel: String = "Select", // TODO, string res
    // select:
    allowSelectTerminalTag: Boolean = true,
    allowSelectNonTerminalTag: Boolean = true,
    defaultSelectedTag: ContextTag? = null,
    /**
     * callback when click a list item, passing current tag, and destination tag and if the destination
     * tag is leaf or not
     */
    onClickListItem: (
        currentTag: ContextTag?,
        clickedTag: ContextTag,
        isClickedTagLeaf: Boolean,
    ) -> Unit = { _, _, _ -> },
    // add:
    allowAddTags: Boolean = false,
    /**
     * after updating data and [tagsTree] pass the new tag as the [defaultSelectedTag] to keep this dialog at the same page
     */
    onAddTag: (tag: ContextTag) -> Unit = {},
) {
    var selectedTag: ContextTag? by remember {
        mutableStateOf(null)
    }
    val selectedSubTree: ContextTagsTree by remember(selectedTag, tagsTree) {
        derivedStateOf {
            selectedTag?.let {
                tagsTree[it].also { subtree ->
                    if (subtree == null) {
                        selectedTag = null
                    }
                }
            } ?: tagsTree
        }
    }
    LaunchedEffect(defaultSelectedTag, tagsTree) {
        if (defaultSelectedTag != null) {
            val tree = tagsTree[defaultSelectedTag] ?: throw IllegalArgumentException(
                "default selected tag is not null and isn't existed in tags tree"
            )
            selectedTag = defaultSelectedTag
        }
    }
    var isAddNewTagInProgress by remember {
        mutableStateOf(false)
    }
    var newTagSegmentText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(isAddNewTagInProgress) {
        newTagSegmentText = ""
    }

    val primaryActionEnabled by remember(
        allowSelectTerminalTag,
        allowSelectNonTerminalTag,
        selectedTag,
    ) {
        derivedStateOf {
            (allowSelectTerminalTag && selectedSubTree.isLeaf) ||
                    (allowSelectNonTerminalTag && selectedSubTree.tag != null && !selectedSubTree.isLeaf)
        }
    }
    var searchQuery by remember {
        mutableStateOf("")
    }
    MDBasicDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .sizeIn(maxWidth = 300.dp)
        ,
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismissRequest,
                primaryActionLabel = primaryActionLabel,
                primaryClickEnabled = primaryActionEnabled,
                onPrimaryClick = {
                    selectedSubTree.tag?.let {
                        onSelect(it, selectedSubTree.isLeaf)
                    }
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
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                },
                tagsTree = tagsTree,
                selectedSubTree = selectedSubTree,
                onSelectTag = {
                    selectedTag = it
                },
                allowAddTags = allowAddTags,
                isAddNewTagInProgress = isAddNewTagInProgress,
                onAddNewTag = {
                    isAddNewTagInProgress = true
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 250.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDHorizontalCardGroup {
                selectedSubTree.nextLevel.forEach { (segment, subTree) ->
                    val (isVisible, isEnabled) = subTree.tag?.let { tag ->
                        val matchSearchQuery = tag.segments.last().tagMatchNormalize.startsWith(searchQuery.tagMatchNormalize)
                        val showDueToSearchQuery = searchFilterHidesTagsOnly || matchSearchQuery
                        val isVisible = showDueToSearchQuery || viewFilter(tag)
                        val isEnabled = isVisible && matchSearchQuery && enableFilter(tag)
                        Pair(isVisible, isEnabled)
                    } ?: Pair(true, true)
                    if(isVisible) {
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
                                selectedTag = subTree.tag
                                onClickListItem(
                                    selectedTag,
                                    subTree.tag!!,
                                    subTree.isLeaf,
                                )
                            }
                        ) {
                            Text(segment)
                        }
                    }
                }
                if (selectedSubTree.isLeaf) {
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
                        newTagSegmentText = it.split(ContextTagSegmentSeparator).joinToString("")
                    },
                    onKeyboardAction = {
                        // cancel
                        if (newTagSegmentText.isNotEmpty()) {// add new Tag
                            val newTag = selectedSubTree.tag?.let {
                                ContextTag(it.segments + newTagSegmentText)
                            } ?: ContextTag(newTagSegmentText)
                            onAddTag(newTag)
                        }
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
                                        // cancel
                                        if (newTagSegmentText.isNotEmpty()) {// add new Tag
                                            val newTag = selectedSubTree.tag?.let {
                                                ContextTag(it.segments + newTagSegmentText)
                                            } ?: ContextTag(newTagSegmentText)
                                            onAddTag(newTag)
                                        }
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
private fun DialogHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,

    tagsTree: ContextTagsTree,
    selectedSubTree: ContextTagsTree,
    onSelectTag: (ContextTag?) -> Unit,

    allowAddTags: Boolean,
    isAddNewTagInProgress: Boolean,
    onAddNewTag: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val randomTagLastSegment by remember {
        derivedStateOf {
            selectedSubTree.nextLevel.values.randomOrNull()?.tag?.segments?.lastOrNull()
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = !selectedSubTree.isRoot,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
            IconButton(
                enabled = !selectedSubTree.isRoot,
                onClick = {
                    selectedSubTree.tag?.let {
                        onSelectTag(
                            if (it.depth == 1) {
                                tagsTree.tag
                            } else {
                                tagsTree[it.parentAtLevel(it.depth.dec())]?.tag
                            }
                        )
                    }
                }
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
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                label = "",
                placeholder = randomTagLastSegment?.let { "e.g $it" /*TODO, string res */ } ?: ""
            )
            selectedSubTree.tag?.let {
                MDContextTagPath(
                    tag = it,
                    onClickSegment = { i, s ->
                        val parentTag = it.parentAtLevel(i.inc())
                        tagsTree[parentTag]?.let {
                            onSelectTag(it.tag)
                        }
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
private fun MDContextTagPath(
    tag: ContextTag,
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
private fun MDContextTagPathPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                MDContextTagPath(
                    tag = ContextTag("object", "food", "vegetables"),
                    onClickSegment = { _, _ ->

                    }
                )

            }
        }
    }
}

@Preview
@Composable
private fun MDContextTagSelectorDialogPreview() {
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
                            ContextTag("object", "food", "fruit"),
                            ContextTag("object", "food", "vegetables"),
                            ContextTag("object", "food", "healthy"),
                            ContextTag("object", "device"),
                            ContextTag("language", "en"),
                        ).asTree()
                    }
                }
                MDContextTagExplorerDialog(
                    showDialog = true,
                    onDismissRequest = {

                    },
                    tagTrailingIcon = {
                        MDIcon(MDIconsSet.Delete)
                    },
                    allowAddTags = true,
                    tagsTree = tree,
                    onSelect = { _, _ -> }
                )
            }
        }
    }
}