package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.pagination.grid.lazyPagingGridItems
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordListItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListDeleteConfirmDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.MDWordsListTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.language_selection_dialog.MDLanguageSelectionDialogRoute
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun MDWordsListScreen(
    uiState: MDWordsListUiState,
    searchQuery: String,
    wordsList: LazyPagingItems<Word>,
    uiActions: MDWordsListUiActions,
    contextTagsSelectionState: MDContextTagsSelectorUiState,
    contextTagsSelectionActions: MDContextTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    lifeMemorizingProbability: Boolean = false,
    currentSpeakingWordId: String? = null,
) {
    val selectedWordsCount by remember(uiState.selectedWords) {
        derivedStateOf {
            uiState.selectedWords.count()
        }
    }
    val selectedWordSpace by uiState.selectedWordSpace.collectAsStateWithLifecycle()
    var now by remember {
        mutableStateOf(Clock.System.now())
    }

    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(lifeMemorizingProbability) {
        if (lifeMemorizingProbability) {
            while (true) {
                delay(1.seconds)
                now = Clock.System.now()
            }
        }
    }


    CompositionLocalProvider(
        LocalLayoutDirection provides selectedWordSpace.direction
    ) {
        Box(
            modifier = modifier,
        ) {
            MDScreen(
                uiState = uiState,
                invalidDataMessage = "Select a language to start", // TODO, string res
                contentWindowInsets = WindowInsets.displayCutout,
                topBar = {
                    MDWordsListTopAppBar(
                        searchQuery = searchQuery,
                        isSelectionModeOn = uiState.isSelectModeOn,
                        language = selectedWordSpace,
                        selectedWordsCount = selectedWordsCount,
                        visibleWordsCount = wordsList.itemCount,
                        onAdjustFilterPreferences = uiActions::onShowViewPreferencesDialog,
                        onSelectLanguagePage = uiActions::onShowLanguageWordSpacesDialog,
                        onDeleteWordSpace = uiActions::onDeleteLanguageWordSpace,
                        contextTagsSelectionState = contextTagsSelectionState,
                        contextTagsSelectionActions = contextTagsSelectionActions,
                        onClearSelection = uiActions::onClearSelection,
                        onDeleteSelection = uiActions::onDeleteSelection,
                        onConfirmAppendContextTagsOnSelectedWords = uiActions::onConfirmAppendContextTagsOnSelectedWords,
                        modifier = Modifier.background(Color.Green),
                        onNavigationIconClick = uiActions::onOpenNavDrawer,
                        onSearchQueryChange = uiActions::onSearchQueryChange
                    )
                },
                floatingActionButton = {
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        AnimatedVisibility(
                            visible = lazyGridState.canScrollBackward,
                            enter = fadeIn() + expandIn(),
                            exit = fadeOut() + shrinkOut(),
                        ) {
                            SmallFloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        lazyGridState.animateScrollToItem(0)
                                    }
                                }
                            ) {
                                MDIcon(MDIconsSet.ArrowUp)
                            }
                        }
                        AnimatedVisibility(
                            visible = selectedWordSpace.valid && (lazyGridState.lastScrolledBackward || !lazyGridState.canScrollBackward),
                            enter = fadeIn() + expandIn(),
                            exit = fadeOut() + shrinkOut(),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                Spacer(modifier = modifier.height(16.dp))
                                SmallFloatingActionButton(
                                    onClick = uiActions::onShowTrainDialog
                                ) {
                                    MDIcon(MDIconsSet.Train)
                                }
                                Spacer(modifier = modifier.height(16.dp))
                                FloatingActionButton(
                                    onClick = {
                                        uiActions.navigateToWordDetails(null)
                                    }
                                ) {
                                    MDIcon(MDIconsSet.Add)
                                }
                            }
                        }
                    }
                }
            ) {
                var expandedWordId: Long? by remember {
                    mutableStateOf(null)
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(250.dp), modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = lazyGridState,
                ) {
                    lazyPagingGridItems(
                        items = wordsList,
                        key = {
                            it.id
                        },
                        emptyItemsPlaceHolder = {
                            Text(
                                text = if (uiState.isViewPreferencesEffectiveFilter) {
                                    "No words matches your filters..."
                                } else {
                                    "No words yet, add some words first"
                                }, // TODO, string res
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    ) { i, word ->
                        val isSelected by remember(uiState.selectedWords) {
                            derivedStateOf { word.id in uiState.selectedWords }
                        }

                        val isExpanded by remember(expandedWordId) {
                            derivedStateOf { expandedWordId == word.id }
                        }

                        val liveMemorizingProbability by remember(
                            key1 = now,
                            key2 = word.memoryDecayFactor,
                            key3 = word.lastTrainTime
                        ) {
                            derivedStateOf {
                                word
                                    .getMemorizingProbabilityOfTime(now)
                                    .times(1000)
                                    .roundToInt()
                                    .div(10f)
                            }
                        }
                        MDWordListItem(
                            word = word,
                            searchQuery = uiState.viewPreferencesQuery,
                            expanded = isExpanded,
                            primaryAction = {
                                if (uiState.isSelectModeOn) {
                                    Checkbox(checked = isSelected, onCheckedChange = null)
                                } else {
                                    Text("$liveMemorizingProbability%")
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
                            isSpeakInProgress = currentSpeakingWordId == word.id.toString(),
                            onSpeak = {
                                uiActions.onSpeakWord(word)
                            }
                        )
                    }
                }
            }
        }
    }
    //// Dialogs:
    // language page:
    MDLanguageSelectionDialogRoute(
        showDialog = uiState.isLanguagesWordSpacesDialogShown,
        onDismissDialog = uiActions::onHideLanguageWordSpacesDialog,
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
        confirmDeleteMessage = "Are you sure you want to delete ${selectedWordSpace.fullDisplayName} (${selectedWordSpace.wordsCount} words)?\n\n this action can not be undone."
    )
}