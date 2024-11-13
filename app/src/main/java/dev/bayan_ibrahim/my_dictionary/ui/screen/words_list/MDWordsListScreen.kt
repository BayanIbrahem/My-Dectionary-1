package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordListItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListDeleteConfirmDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListLanguageSelectionPageDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListViewPreferencesDialog

@Composable
fun MDWordsListScreen(
    uiState: MDWordsListUiState,
    uiActions: MDWordsListUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        invalidDataMessage = "Select a language to start", // TODO, string res
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MDWordsListTopAppBar(
                isSelectionModeOn = uiState.isSelectModeOn,
                language = uiState.selectedWordSpace.language,
                selectedWordsCount = uiState.selectedWords.count(),
                visibleWordsCount = uiState.words.count(),
                totalWordsCount = uiState.words.count(), // TODO, pass total words count,
                onAdjustFilterPreferences = uiActions::onShowViewPreferencesDialog,
                onSelectLanguagePage = uiActions::onShowLanguageWordSpacesDialog,
                onDeleteWordSpace = uiActions::onDeleteLanguageWordSpace,
                onClearSelection = uiActions::onClearSelection,
                onSelectAll = uiActions::onSelectAll,
                onInvertSelection = uiActions::onInvertSelection,
                onDeleteSelection = uiActions::onDeleteSelection
            )
        },
        floatingActionButton = {
            if (uiState.selectedWordSpace.valid) {
                FloatingActionButton(
                    onClick = {
                        uiActions.navigateToWordDetails(null)
                    }
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    ) {
        var expandedWordId: Long? by remember {
            mutableStateOf(null)
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.words.isEmpty()) {
                item(
                    key = -4,
                    contentType = "placeholder"
                ) {
                    Text(
                        text = if (uiState.preferencesState.effectiveFilter) {
                            "No words matches your filters..."
                        } else {
                            "No words yet, add some words first"
                        }, // TODO, string res
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                items(
                    items = uiState.words,
                    key = { it.id },
                    contentType = { "word" }
                ) { word ->
                    val isSelected by remember {
                        derivedStateOf { word.id in uiState.selectedWords }
                    }

                    val isExpanded by remember {
                        derivedStateOf { expandedWordId == word.id }
                    }

                    MDWordListItem(
                        word = word,
                        expanded = isExpanded,
                        primaryAction = {
                            if(uiState.isSelectModeOn) {
                                Checkbox(checked = isSelected, onCheckedChange = null)
                            }
                        },
                        onClickHeader = {
                            expandedWordId = word.id
                        },
                        onClick = {
                            uiActions.onClickWord(word.id)
                        },
                        onLongClick = {
                            uiActions.onLongClickWord(word.id)
                        },
                    )
                }
            }
        }
    }
    //// Dialogs:
    // language page:
    MDWordsListLanguageSelectionPageDialog(
        showDialog = uiState.isLanguagesWordSpacesDialogShown,
        onDismissRequest = uiActions::onHideLanguageWordSpacesDialog,
        query = uiState.languagesWordSpaceSearchQuery,
        onQueryChange = uiActions::onLanguageWordSpaceSearchQueryChange,
        languagesWithWords = uiState.activeLanguagesWordSpaces,
        languagesWithoutWords = uiState.inactiveLanguagesWordSpaces,
        onSelectWordSpace = uiActions::onSelectLanguageWordSpace
    )
    // delete words confirm dialog:
    MDWordsListDeleteConfirmDialog(
        showDialog = uiState.isSelectedWordsDeleteDialogShown,
        isDeleteRunning = uiState.isSelectedWordsDeleteProcessRunning,
        onCancel = uiActions::onCancelDeleteSelection,
        onConfirm = uiActions::onConfirmDeleteSelection,
        title = "Delete Words", // TODO, string res
        runningDeleteMessage = "Deletion process is running please wait...",// TODO, string res
        confirmDeleteMessage = "Are you sure you want to delete ${uiState.selectedWords.count()} words?\n\n this action can not be undone."
    )
    // delete word space confirm dialog:
    MDWordsListDeleteConfirmDialog(
        showDialog = uiState.isSelectedWordsDeleteDialogShown,
        isDeleteRunning = uiState.isLanguageWordSpaceDeleteProcessRunning,
        onCancel = uiActions::onCancelDeleteLanguageWordSpace,
        onConfirm = uiActions::onConfirmDeleteLanguageWordSpace,
        title = "Delete Language", // TODO, string res
        runningDeleteMessage = "Deletion process is running please wait...",// TODO, string res
        confirmDeleteMessage = "Are you sure you want to delete ${uiState.selectedWordSpace.language.fullDisplayName} (${uiState.selectedWordSpace.wordsCount} words)?\n\n this action can not be undone."
    )
    // view preferences dialog:
    MDWordsListViewPreferencesDialog(
        showDialog = uiState.isViewPreferencesDialogShown,
        onDismissRequest = uiActions::onHideViewPreferencesDialog,
        preferences = uiState.preferencesState,
        onSearchQueryChange = uiActions::onSearchQueryChange,
        onSelectSearchTarget = uiActions::onSelectSearchTarget,
        tagSearchQuery = uiState.tagSearchQuery,
        tagsSuggestions = uiState.tagsSuggestions,
        onTagSearchQueryChange = uiActions::onTagSearchQueryChange,
        onSelectTag = uiActions::onSelectTag,
        onRemoveTag = uiActions::onRemoveTag,
        onToggleSelectedTags = uiActions::onToggleIncludeSelectedTags,
        onSelectLearningGroup = uiActions::onSelectLearningGroup,
        onSelectSortBy = uiActions::onSelectWordsSortBy,
        onSelectSortByOrder = uiActions::onSelectWordsSortByOrder
    )
}