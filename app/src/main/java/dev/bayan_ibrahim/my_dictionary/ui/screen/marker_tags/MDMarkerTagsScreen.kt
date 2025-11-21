package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorBusinessUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component.MDMarkerTagListItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component.MDMarkerTagsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MDMarkerTagsScreen(
    uiState: MDMarkerTagsUiState,
    markerTags: PersistentList<Tag>,
    nonMarkerTagsState: MDTagsSelectorUiState,
    nonMarkerTagsActions: MDTagsSelectorUiActions,
    uiActions: MDMarkerTagsUiActions,
    modifier: Modifier = Modifier,
) {
    var showTagsSelectorDialog by remember {
        mutableStateOf(false)
    }
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDMarkerTagsTopAppBar(
                onAddTag = { showTagsSelectorDialog = true }, onNavigateBack = uiActions::onPop
            )
        },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (markerTags.isEmpty()) {
                item {
                    Text(firstCapPluralsResource(R.plurals.marker_tag, 0))
                }
            }
            items(items = markerTags) { tag ->
                MDMarkerTagListItem(
                    modifier = Modifier.animateItem(),
                    tag = tag,
                    onChangeColor = {
                        uiActions.updateTag(tag.onCopy(color = it))
                    },
                    onToggleInheritedMarkerColor = {
                        uiActions.updateTag(tag.onCopy(passColorToChildren = it))
                    },
                    onRemoveTag = {
                        uiActions.removeTag(tag)
                    },
                    onRemoveMarker = {
                        uiActions.updateTag(tag.onCopy(color = null))
                    }
                )
            }
        }
        // dialog:
        if (showTagsSelectorDialog) {
            MDTagsSelectorRoute(
                isDialog = true,
                isSelectEnabled = true,
                selectedTagsMaxSize = 1,
                isAddEnabled = false,
                isEditEnabled = false,
                isDeleteEnabled = false,
                isDeleteSubtreeEnabled = false,
                onPopOrDismissDialog = {
                    showTagsSelectorDialog = false
                },
                onConfirmSelectedTags = {

                }
            )
        }
    }
}

@Preview
@Composable
private fun MDMarkerTagsScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDMarkerTagsScreen(
                    uiState = MDMarkerTagsMutableUiState().apply {
                        onExecute { true }
                    },
                    markerTags = persistentListOf(),
                    nonMarkerTagsState = MDTagsSelectorMutableUiState(),
                    nonMarkerTagsActions = MDTagsSelectorUiActions(
                        navigationActions = object : MDTagsSelectorNavigationUiActions {},
                        businessActions = object : MDTagsSelectorBusinessUiActions {
                            override fun onClickTag(tag: Tag) {}

                            override fun onToggleSelectTag(tag: Tag) {}

                            override fun onSelectCurrentTag() {}

                            override fun onUnSelectTag(tag: Tag) {}

                            override fun onSetInitialSelectedTags(tags: Collection<Tag>) {}

                            override fun clearSelectedTags() {}

                            override fun onAddChild(tag: Tag) {}

                            override fun onAddNewTag(label: String) {}

                            override fun onDeleteTag(tag: Tag) {}

                            override fun onNavigateUp() {}

                            override fun onResetToRoot() {}

                            override fun onSetAllowedTagsFilter(filter: (Tag) -> Boolean) {}

                            override fun onResetAllowedTagsFilter() {}
                            override fun onSetForbiddenTagsFilter(filter: (Tag) -> Boolean) {}
                            override fun onResetForbiddenTagsFilter() {}
                            override fun onResetTagsFilter() {}
                            override fun onSearchQueryChange(query: String) {}
                            override fun refreshCurrentTree() {}
                        }),
                    uiActions = MDMarkerTagsUiActions(
                        object : MDMarkerTagsNavigationUiActions, MDAppNavigationUiActions {
                            override fun onOpenNavDrawer() {}
                            override fun onCloseNavDrawer() {}

                        },
                        object : MDMarkerTagsBusinessUiActions {
                            override fun updateTag(tag: Tag) {}
                            override fun removeTag(tag: Tag) {}
                            override fun onUpdateSelectedTags(selectedTags: List<Tag>) {}
                        },
                    )
                )
            }
        }
    }
}
