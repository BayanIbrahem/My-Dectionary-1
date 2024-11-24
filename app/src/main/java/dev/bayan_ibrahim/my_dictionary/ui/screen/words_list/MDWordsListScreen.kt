package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordListItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListDeleteConfirmDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListLanguageSelectionPageDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences.MDWordsListTrainPreferencesDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.view_preferences.MDWordsListViewPreferencesDialog

@Composable
fun MDWordsListScreen(
    uiState: MDWordsListUiState,
    wordsList: List<Word>,
    uiActions: MDWordsListUiActions,
    modifier: Modifier = Modifier,
) {
    val selectedWordsCount by remember(uiState.selectedWords) {
        derivedStateOf {
            uiState.selectedWords.count()
        }
    }
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        invalidDataMessage = "Select a language to start", // TODO, string res
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MDWordsListTopAppBar(
                isSelectionModeOn = uiState.isSelectModeOn,
                language = uiState.selectedWordSpace.language,
                selectedWordsCount = selectedWordsCount,
                visibleWordsCount = wordsList.count(),
                totalWordsCount = wordsList.count(), // TODO, pass total words count,
                onTrainVisibleWords = uiActions::onShowTrainPreferencesDialog,
                onAdjustFilterPreferences = uiActions::onShowViewPreferencesDialog,
                onSelectLanguagePage = uiActions::onShowLanguageWordSpacesDialog,
                onDeleteWordSpace = uiActions::onDeleteLanguageWordSpace,
                onClearSelection = uiActions::onClearSelection,
                onSelectAll = uiActions::onSelectAll,
                onInvertSelection = uiActions::onInvertSelection,
                onDeleteSelection = uiActions::onDeleteSelection,
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
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(250.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (wordsList.isEmpty()) {
                item(
                    key = -4,
                    contentType = "placeholder",
                    span = {
                        GridItemSpan(this.maxLineSpan)
                    }
                ) {
                    Text(
                        text = if (uiState.viewPreferencesState.effectiveFilter) {
                            "No words matches your filters..."
                        } else {
                            "No words yet, add some words first"
                        }, // TODO, string res
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                items(
                    items = wordsList,
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
                            if (uiState.isSelectModeOn) {
                                Checkbox(checked = isSelected, onCheckedChange = null)
                            }
                        },
                        onClickHeader = {
                            if (expandedWordId == word.id) {
                                expandedWordId = null
                            } else {
                                expandedWordId = word.id
                            }
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
        confirmDeleteMessage = "Are you sure you want to delete $selectedWordsCount words?\n\nthis action can not be undone." // TODO, string res
    )
    // delete word space confirm dialog:
    MDWordsListDeleteConfirmDialog(
        showDialog = uiState.isLanguageWordSpaceDeleteDialogShown,
        isDeleteRunning = uiState.isLanguageWordSpaceDeleteProcessRunning,
        onCancel = uiActions::onCancelDeleteLanguageWordSpace,
        onConfirm = uiActions::onConfirmDeleteLanguageWordSpace,
        title = "Delete Language", // TODO, string res
        runningDeleteMessage = "Deletion process is running please wait...",// TODO, string res
        confirmDeleteMessage = "Are you sure you want to delete ${uiState.selectedWordSpace.language.fullDisplayName} (${uiState.selectedWordSpace.wordsCount} words)?\n\n this action can not be undone."
    )
    // view preferences dialog:
    MDWordsListViewPreferencesDialog(
        state = uiState.viewPreferencesState,
        tagSearchQuery = uiState.tagSearchQuery,
        tagsSuggestions = uiState.tagsSuggestions,
        actions = uiActions,
    )

    // view preferences dialog:
    MDWordsListTrainPreferencesDialog(
        state = uiState.trainPreferencesState,
        actions = uiActions,
    )
}