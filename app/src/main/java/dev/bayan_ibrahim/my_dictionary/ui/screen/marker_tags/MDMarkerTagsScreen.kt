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
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagExplorerDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorBusinessUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component.MDMarkerTagListItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component.MDMarkerTagsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MDMarkerTagsScreen(
    uiState: MDMarkerTagsUiState,
    markerTags: PersistentList<ContextTag>,
    nonMarkerTagsState: MDContextTagsSelectorUiState,
    nonMarkerTagsActions: MDContextTagsSelectorUiActions,
    uiActions: MDMarkerTagsUiActions,
    modifier: Modifier = Modifier,
) {
    var showExplorerDialog by remember {
        mutableStateOf(false)
    }
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDMarkerTagsTopAppBar(
                onAddTag = { showExplorerDialog = true }, onNavigateBack = uiActions::onPop
            )
        },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (markerTags.isEmpty()) {
                item {
                    Text("No marker Tags Added") // TODO, string res
                }
            }
            items(items = markerTags) { tag ->
                MDMarkerTagListItem(modifier = Modifier.animateItem(), tag = tag, onChangeColor = {
                    uiActions.updateTag(tag.copy(color = it))
                }, onToggleInheritedMarkerColor = {
                    uiActions.updateTag(tag.copy(passColorToChildren = it))
                }, onRemoveTag = {
                    uiActions.removeTag(tag)
                }, onRemoveMarker = {
                    uiActions.updateTag(tag.copy(color = null))
                })
            }
        }
        // dialog:
        MDContextTagExplorerDialog(
            showDialog = showExplorerDialog,
            onDismissRequest = { showExplorerDialog = false },
            state = nonMarkerTagsState,
            actions = nonMarkerTagsActions,
        )
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
                    nonMarkerTagsState = MDContextTagsSelectorMutableUiState(),
                    nonMarkerTagsActions = MDContextTagsSelectorUiActions(navigationActions = object : MDContextTagsSelectorNavigationUiActions {},
                        businessActions = object : MDContextTagsSelectorBusinessUiActions {
                            override fun onClickTag(tag: ContextTag) {}

                            override fun onSelectTag(tag: ContextTag) {}

                            override fun onSelectCurrentTag() {}

                            override fun onUnSelectTag(tag: ContextTag) {}

                            override fun onSetInitialSelectedTags(tags: Collection<ContextTag>) {}

                            override fun clearSelectedTags() {}

                            override fun onAddNewContextTag(tag: ContextTag) {}

                            override fun onAddNewContextTag(segment: String) {}

                            override fun onDeleteContextTag(tag: ContextTag) {}

                            override fun onNavigateUp() {}

                            override fun onResetToRoot() {}

                            override fun onSetAllowedTagsFilter(filter: (ContextTag) -> Boolean) {}

                            override fun onResetAllowedTagsFilter() {}
                            override fun onSetForbiddenTagsFilter(filter: (ContextTag) -> Boolean) {}
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
                            override fun updateTag(tag: ContextTag) {}
                            override fun removeTag(tag: ContextTag) {}
                        },
                    )
                )
            }
        }
    }
}
