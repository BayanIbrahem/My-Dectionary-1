package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicIconDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorViewModel
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
    onAdjustFilterPreferences: () -> Unit,
    onSelectLanguagePage: () -> Unit,
    onDeleteWordSpace: () -> Unit,
    onNavigationIconClick: () -> Unit,
    // selection mode actions
    tagsSelectionState: MDTagsSelectorUiState,
    tagsSelectionActions: MDTagsSelectorUiActions,
    onClearSelection: () -> Unit,
    onDeleteSelection: () -> Unit,
    onConfirmAppendTagsOnSelectedWords: (selectedTags: List<Tag>) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
    ) {
        AnimatedVisibility(!isSelectionModeOn) {
            WordsListTopAppBarNormalMode(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                language = language,
                visibleWordsCount = visibleWordsCount,
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
                tagsSelectionState = tagsSelectionState,
                tagsSelectionActions = tagsSelectionActions,
                onConfirmAppendTagsOnSelectedWords = onConfirmAppendTagsOnSelectedWords,
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
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    language: Language,
    visibleWordsCount: Int,
    onAdjustFilterPreferences: () -> Unit,
    onSelectLanguagePage: () -> Unit,
    onDeleteWordSpace: () -> Unit,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember {
        mutableStateOf(searchQuery)
    }
    val searchFieldFocusRequester by remember {
        derivedStateOf {
            FocusRequester()
        }
    }
    var searchFieldHasFocus by remember {
        mutableStateOf(false)
    }
    val searchFieldVisible by remember(searchQuery, searchFieldHasFocus) {
        derivedStateOf {
            searchFieldHasFocus
        }
    }
    val searchFieldNotEmpty by remember(searchQuery) {
        derivedStateOf {
            searchQuery.isNotBlank()
        }
    }

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigationIconClick
            ) {
                MDIcon(MDIconsSet.Menu)
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("${language.localDisplayName} ") // has a trailing space
                Text(
                    text = firstCapPluralsResource(R.plurals.word, visibleWordsCount),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(
                visible = !searchFieldVisible,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                IconButton(
                    onClick = {
                        searchFieldFocusRequester.requestFocus()
                    }
                ) {
                    BadgedBox(
                        badge = {
                            if (searchFieldNotEmpty) {
                                Badge()
                            }
                        }
                    ) {
                        MDIcon(MDIconsSet.Search)
                    }
                }
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
                        text = firstCapStringResource(R.string.select_x, firstCapStringResource(R.string.language)),
                        onClick = {
                            dismiss()
                            onSelectLanguagePage()
                        },
                    )

                    MenuItem(
                        leadingIcon = MDIconsSet.DeletePermanent,
                        text = firstCapStringResource(R.string.delete_x, firstCapStringResource(R.string.word_space)),
                        onClick = {
                            dismiss()
                            onDeleteWordSpace()
                        },
                        important = true,
                    )
                }
            }
        }
        val factor by animateFloatAsState(if (searchFieldVisible) 1f else 0f)
        val height by animateDpAsState(if (searchFieldVisible) 92.dp else 0.dp)
        MDWordFieldTextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChange(it)
                searchQuery = it
            },
            leadingIcon = MDIconsSet.SearchList,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(height)
                .graphicsLayer {
                    alpha = factor
                }
                .fillMaxWidth()
                .focusRequester(searchFieldFocusRequester),
            onFocusEvent = {
                searchFieldHasFocus = it.isFocused
            },
            showLabelOnEditMode = true,
            label = firstCapStringResource(R.string.search_query),
            placeholder = firstCapStringResource(R.string.eg_x, firstCapStringResource(R.string.car)),
            showTrailingActionsIfNotFocused = true,
            showTrailingActionsIfBlank = true,
            onKeyboardAction = { searchFieldFocusRequester.freeFocus() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordsListTopAppBarSelectionMode(
    selectedWordsCount: Int,
    totalWordsCount: Int,
    onClearSelection: () -> Unit,
    onDeleteSelection: () -> Unit,
    tagsSelectionState: MDTagsSelectorUiState,
    tagsSelectionActions: MDTagsSelectorUiActions,
    onConfirmAppendTagsOnSelectedWords: (selectedTags: List<Tag>) -> Unit,
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
            Text(stringResource(R.string.x_selected_of_y, selectedWordsCount, totalWordsCount))
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
                        text = eachFirstCapStringResource(R.string.delete_selection),
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
        tagsSelectionState = tagsSelectionState,
        tagsSelectionActions = tagsSelectionActions,
        onConfirm = onConfirmAppendTagsOnSelectedWords,
    )
}

@Composable
private fun ExtraTagsDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    selectedWordsCount: Int,
    tagsSelectionState: MDTagsSelectorUiState,
    tagsSelectionActions: MDTagsSelectorUiActions,
    onConfirm: (selectedTags: List<Tag>) -> Unit,
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
                Text(text = firstCapStringResource(R.string.append_x, firstCapStringResource(R.string.tag)))
                Text(
                    text = firstCapStringResource(R.string.x_selected, firstCapPluralsResource(R.plurals.word, selectedWordsCount)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismissRequest,
                primaryClickEnabled = tagsSelectionState.selectedTags.isNotEmpty(),
                onPrimaryClick = {
                    onConfirm(tagsSelectionState.selectedTags)
                    tagsSelectionActions.clearSelectedTags()
                    tagsSelectionActions.onResetToRoot()
                },
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MDTagsSelector(
                state = tagsSelectionState,
                actions = tagsSelectionActions
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
                val viewModel: MDTagsSelectorViewModel = hiltViewModel()
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
                    tagsSelectionState = MDTagsSelectorMutableUiState(),
                    tagsSelectionActions = viewModel.getUiActions(object : MDTagsSelectorNavigationUiActions {}),
                    onConfirmAppendTagsOnSelectedWords = {},
                    onNavigationIconClick = {},
                    searchQuery = "",
                    onSearchQueryChange = {}
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
