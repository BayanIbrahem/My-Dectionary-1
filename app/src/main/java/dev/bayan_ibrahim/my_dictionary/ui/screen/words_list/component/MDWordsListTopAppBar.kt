package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicIconDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorViewModel
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

/**
 * this require checking for the value [isSelectionModeOn] before executing functions to prevent rapid clicks during animation
 */
@Composable
fun MDWordsListTopAppBar(
    isSelectionModeOn: Boolean,
    language: Language,
    selectedWordsCount: Int,
    visibleWordsCount: Int,
    // normal mode actions,
    onTrainVisibleWords: () -> Unit,
    onAdjustFilterPreferences: () -> Unit,
    onSelectLanguagePage: () -> Unit,
    onDeleteWordSpace: () -> Unit,
    onNavigationIconClick: () -> Unit,
    // selection mode actions
    contextTagsSelectionState: MDContextTagsSelectorUiState,
    contextTagsSelectionActions: MDContextTagsSelectorUiActions,
    onClearSelection: () -> Unit,
    onDeleteSelection: () -> Unit,
    onConfirmAppendContextTagsOnSelectedWords: (selectedTags: List<ContextTag>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
    ) {
        AnimatedVisibility(!isSelectionModeOn) {
            WordsListTopAppBarNormalMode(
                language = language,
                visibleWordsCount = visibleWordsCount,
                onTrainVisibleWords = onTrainVisibleWords,
                onAdjustFilterPreferences = onAdjustFilterPreferences,
                onSelectLanguagePage = onSelectLanguagePage,
                onDeleteWordSpace = onDeleteWordSpace,
                modifier = modifier,
                onNavigationIconClick = onNavigationIconClick,
            )
        }
        AnimatedVisibility(isSelectionModeOn) {
            WordsListTopAppBarSelectionMode(
                selectedWordsCount = selectedWordsCount,
                totalWordsCount = visibleWordsCount,
                onClearSelection = onClearSelection,
                onDeleteSelection = onDeleteSelection,
                contextTagsSelectionState = contextTagsSelectionState,
                contextTagsSelectionActions = contextTagsSelectionActions,
                onConfirmAppendContextTagsOnSelectedWords = onConfirmAppendContextTagsOnSelectedWords,
                modifier = modifier
            )
        }
    }
}

private val topAppBarHeight: Dp = 48.dp

// according to top app bar height, this offset will make the menu has 0 padding with the bar (from top) and the screen( from end) with no padding
private val defaultMenuOffset = DpOffset(14.dp, 14.dp)
private val defaultMenuPadding: Dp = 8.dp
private val menuOffset = defaultMenuOffset + DpOffset(-defaultMenuPadding, defaultMenuPadding)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordsListTopAppBarNormalMode(
    language: Language,
    visibleWordsCount: Int,
    onTrainVisibleWords: () -> Unit,
    onAdjustFilterPreferences: () -> Unit,
    onSelectLanguagePage: () -> Unit,
    onDeleteWordSpace: () -> Unit,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDTopAppBar(
        isTopLevel = true,
        onNavigationIconClick = onNavigationIconClick,
        title = {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("${language.localDisplayName} ") // has a trailing space
                Text(
                    text = "$visibleWordsCount words", // TODO, string res
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        expandedHeight = topAppBarHeight,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        actions = {
            IconButton(
                onClick = {
                    onTrainVisibleWords()
                    // adjust filter preferences
                },
            ) {
                MDIcon(MDIconsSet.Train)
            }
            IconButton(
                onClick = {
                    onAdjustFilterPreferences()
                    // adjust filter preferences
                },
            ) {
                MDIcon(MDIconsSet.Filter)
            }
            var expanded by remember {
                mutableStateOf(false)
            }
            val dismiss: () -> Unit by remember {
                derivedStateOf { { expanded = false } }
            }
            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                MDBasicIconDropDownMenu(
                    expanded = expanded,
                    onDismissRequest = dismiss,
                    menuOffset = menuOffset,
                    icon = {
                        MDIcon(MDIconsSet.MoreVert)
                    }
                ) {
                    MenuItem(
                        leadingIcon = MDIconsSet.LanguageWordSpace,
                        text = "Select language page", // TODO, string res
                        onClick = {
                            dismiss()
                            onSelectLanguagePage()
                        },
                    )

                    MenuItem(
                        leadingIcon = MDIconsSet.DeletePermanent,
                        text = "Delete word space", // TODO, string res
                        onClick = {
                            dismiss()
                            onDeleteWordSpace()
                        },
                        important = true,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordsListTopAppBarSelectionMode(
    selectedWordsCount: Int,
    totalWordsCount: Int,
    onClearSelection: () -> Unit,
    onDeleteSelection: () -> Unit,
    contextTagsSelectionState: MDContextTagsSelectorUiState,
    contextTagsSelectionActions: MDContextTagsSelectorUiActions,
    onConfirmAppendContextTagsOnSelectedWords: (selectedTags: List<ContextTag>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasSelected by remember(selectedWordsCount) {
        derivedStateOf { selectedWordsCount > 0 }
    }
    var showSelectedTagsDialog by remember {
        mutableStateOf(false)
    }

    MDTopAppBar(
        isTopLevel = true, // this value is overwritten by navigation icon
        onNavigationIconClick = {}, // cause it has a navigation icon
        title = {
            Text("$selectedWordsCount selected of $totalWordsCount") // TODO, string res
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onClearSelection
            ) {
                MDIcon(MDIconsSet.Close)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        expandedHeight = topAppBarHeight,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        actions = {
            var expanded by remember {
                mutableStateOf(false)
            }
            val dismiss: () -> Unit by remember {
                derivedStateOf { { expanded = false } }
            }
            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                MDBasicIconDropDownMenu(
                    expanded = expanded,
                    onDismissRequest = dismiss,
                    menuOffset = menuOffset,
                    icon = {
                        MDIcon(MDIconsSet.MoreVert)
                    }
                ) {
                    MenuItem(
                        leadingIcon = MDIconsSet.Delete,
                        text = "Delete Selection", // TODO, string res
                        onClick = {
                            dismiss()
                            onDeleteSelection()
                        },
                        enabled = hasSelected,
                        important = true,
                    )
                    MenuItem(
                        leadingIcon = MDIconsSet.WordTag, // TODO, icon res
                        text = "Append Context tags",
                        onClick = {
                            dismiss()
                            showSelectedTagsDialog = true
                        },
                        enabled = hasSelected,
                    )
                }
            }
        },
    )
    // Dialog:
    ExtraTagsDialog(
        showDialog = showSelectedTagsDialog,
        onDismissRequest = { showSelectedTagsDialog = false },
        selectedWordsCount = selectedWordsCount,
        contextTagsSelectionState = contextTagsSelectionState,
        contextTagsSelectionActions = contextTagsSelectionActions,
        onConfirm = onConfirmAppendContextTagsOnSelectedWords,
    )
}

@Composable
private fun ExtraTagsDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    selectedWordsCount: Int,
    contextTagsSelectionState: MDContextTagsSelectorUiState,
    contextTagsSelectionActions: MDContextTagsSelectorUiActions,
    onConfirm: (selectedTags: List<ContextTag>) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDAlertDialog(
        modifier = modifier.widthIn(max = 300.dp),
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        headerModifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        title = {
            Column {
                Text(text = "Append Tags") // TODO, string res
                Text(
                    text = "Selected Words $selectedWordsCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                ) // TODO, string res
            }
        },
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismissRequest,
                primaryClickEnabled = contextTagsSelectionState.selectedTags.isNotEmpty(),
                onPrimaryClick = {
                    onConfirm(contextTagsSelectionState.selectedTags)
                    contextTagsSelectionActions.clearSelectedTags()
                    contextTagsSelectionActions.onResetToRoot()
                },
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MDContextTagsSelector(
                state = contextTagsSelectionState,
                actions = contextTagsSelectionActions
            )
        }
    }

}

@Composable
private fun ColumnScope.MenuItem(
    text: String,
    leadingIcon: MDIconsSet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    important: Boolean = false,
) {
    DropdownMenuItem(
        text = {
            Text(text)
        },
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            MDIcon(leadingIcon, contentDescription = null)
        },
        enabled = enabled,
        colors = if (important) {
            val error = MaterialTheme.colorScheme.error
            val disabledError = error.copy(0.38f)
            MenuDefaults.itemColors(
                textColor = error,
                leadingIconColor = error,
                disabledTextColor = disabledError,
                disabledLeadingIconColor = disabledError,
            )
        } else MenuDefaults.itemColors()
    )
}

@Preview
@Composable
private fun WordsListTopAppBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
                var selectionMode by remember {
                    mutableStateOf(true)
                }
                val viewModel: MDContextTagsSelectorViewModel = hiltViewModel()
                MDWordsListTopAppBar(
                    isSelectionModeOn = selectionMode,
                    language = Language(code = "ar", selfDisplayName = "العربية", localDisplayName = "Arabic"),
                    selectedWordsCount = 5,
                    visibleWordsCount = 100,
                    onAdjustFilterPreferences = {
                        selectionMode = true
                    },
                    onSelectLanguagePage = {},
                    onDeleteWordSpace = {},
                    onClearSelection = {
                        selectionMode = false
                    },
                    onDeleteSelection = {},
                    onTrainVisibleWords = {},
                    contextTagsSelectionState = MDContextTagsSelectorMutableUiState(),
                    contextTagsSelectionActions = viewModel.getUiActions(object : MDContextTagsSelectorNavigationUiActions {}),
                    onConfirmAppendContextTagsOnSelectedWords = {},
                    onNavigationIconClick = {},
                )
//                WordsListTopAppBar(
//                    isSelectionModeOn = false,
//                    selectedWordsCount = 0,
//                    totalWordsCount = 10,
//                    onClearSelection = {},
//                    onSelectAll = {},
//                    onInvertSelection = {},
//                    onDeleteSelection = {},
//                )
            }
        }
    }
}
