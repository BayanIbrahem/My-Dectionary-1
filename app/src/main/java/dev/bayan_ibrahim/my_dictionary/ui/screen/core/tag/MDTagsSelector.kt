package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

fun LazyListScope.tagsSelector(
    state: MDTagsSelectorUiState,
    actions: MDTagsSelectorUiActions,
    /**
     * toggle permission to create new tags to database
     */
    allowAddTags: Boolean = true,
    /**
     * toggle permissions to remove tags to database (permanently)
     */
    allowRemoveTags: Boolean = true,
    /**
     * togglePermission to edit selected tags
     */
    allowEditTags: Boolean = true,
    label: @Composable () -> String = { "Context Tags" },
    spacedBy: Dp = 0.dp,
    showHorizontalDivider: Boolean = false,
    showTitle: Boolean = false,
) {
    if (showTitle) {
        item {
            Text(label(), style = MDTextFieldDefaults.labelStyle)
        }
    }
    itemsIndexed(state.selectedTags) { i, tag ->
        val onLongClick: () -> Unit by remember(allowEditTags) {
            derivedStateOf {
                if (allowEditTags) {
                    {
                        actions.onUnSelectTag(tag)
                    }
                } else {
                    {}
                }
            }
        }
        MDTagListItem(
            modifier = Modifier.animateItem(),
            tag = tag,
            tagOrder = i.inc(),
            onClick = {}, // must has onClick to be onLongClick enabled
            onLongClick = onLongClick
        )
        if (i != state.selectedTags.count().dec()) {
            Spacer(modifier = Modifier.height(spacedBy))
        }
    }

    if (allowEditTags) {
        item {
            var showExploreDialog by remember {
                mutableStateOf(false)
            }
            Spacer(modifier = Modifier.height(spacedBy))
            MDNewTagListItem(
                modifier = Modifier.animateItem(),
                onAddNewTagClick = {
                    showExploreDialog = true
                }
            )
            MDTagExplorerDialog(
                showDialog = showExploreDialog,
                onDismissRequest = { showExploreDialog = false },
                state = state,
                actions = actions,
                allowAddTags = allowAddTags,
                allowRemoveTags = allowRemoveTags,
            )
        }
    }
    if (showHorizontalDivider) {
        item {
            HorizontalDivider()
        }
    }
}

@Composable
fun ColumnScope.MDTagsSelector(
    state: MDTagsSelectorUiState,
    actions: MDTagsSelectorUiActions,
    /**
     * toggle permission to create new tags to database
     */
    allowAddTags: Boolean = true,
    /**
     * toggle permissions to remove tags to database (permanently)
     */
    allowRemoveTags: Boolean = true,
    /**
     * togglePermission to edit selected tags
     */
    allowEditTags: Boolean = true,
) {
    var showExploreDialog by remember {
        mutableStateOf(false)
    }
    MDTagExplorerDialog(
        showDialog = showExploreDialog,
        onDismissRequest = { showExploreDialog = false },
        state = state,
        actions = actions,
        allowAddTags = allowAddTags,
        allowRemoveTags = allowRemoveTags,
    )
    val onLongClick: (Tag) -> Unit by remember(allowEditTags) {
        derivedStateOf {
            if (allowEditTags) actions::onUnSelectTag
            else {
                {}
            }
        }
    }
    state.selectedTags.forEachIndexed { i, tag ->
        MDTagListItem(
            tag = tag,
            tagOrder = i.inc(),
            onClick = {}, // must has onClick to be onLongClick enabled
            onLongClick = {
                onLongClick(tag)
            }
        )
    }
    AnimatedVisibility(allowEditTags) {
        MDNewTagListItem(
            onAddNewTagClick = {
                showExploreDialog = true
            }
        )
    }
}

@Composable
fun MDTagListItem(
    tag: Tag,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    tagOrder: Int? = null,
) {
    MDTagListItem(
        value = "${tagOrder?.let { "$it. " } ?: ""}${tag.value}", // TODO, string res
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@Composable
fun MDNewTagListItem(
    onAddNewTagClick: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * order of this tag if it is used in the bottom of tags list
     */
    tagOrder: Int? = null,
) {
    MDTagListItem(
        value = "${tagOrder?.let { "$it. " } ?: ""}Add New Context Tag", // TODO, string res
        modifier = modifier,
        onClick = onAddNewTagClick,
    )
}

@Composable
private fun MDTagListItem(
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    MDVerticalCard(
        modifier = modifier,
        headerModifier = Modifier,
        footerModifier = Modifier,
        contentModifier = Modifier,
        cardClickable = onClick != null,
        onClick = {
            onClick?.invoke()
        },
        onLongClick = {
            onLongClick?.invoke()
        },
        colors = MDCardDefaults.colors(
            contentContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDIcon(MDIconsSet.WordTag)
            Text(text = value, style = MDTextFieldDefaults.textStyle)
        }
    }
}