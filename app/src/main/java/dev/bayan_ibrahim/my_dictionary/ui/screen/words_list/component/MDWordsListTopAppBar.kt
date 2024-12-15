package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicIconDropDownMenu
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentOutlinedPainter

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
    // selection mode actions
    onClearSelection: () -> Unit,
    onDeleteSelection: () -> Unit,
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
            )
        }
        AnimatedVisibility(isSelectionModeOn) {
            WordsListTopAppBarSelectionMode(
                selectedWordsCount = selectedWordsCount,
                totalWordsCount = visibleWordsCount,
                onClearSelection = onClearSelection,
                onDeleteSelection = onDeleteSelection,
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
    modifier: Modifier = Modifier,
) {
    TopAppBar(
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
                Icon(
                    painter = MDIconsSet.Train.currentOutlinedPainter,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    onAdjustFilterPreferences()
                    // adjust filter preferences
                },
            ) {
                Icon(MDIconsSet.Filter.currentOutlinedPainter, null)
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
                        Icon(
                            painter = MDIconsSet.MoreVert.currentOutlinedPainter,
                            contentDescription = null
                        )
                    }
                ) {
                    MenuItem(
                        leadingIcon = MDIconsSet.LanguageWordSpace.currentOutlinedPainter,
                        text = "Select language page", // TODO, string res
                        onClick = {
                            dismiss()
                            onSelectLanguagePage()
                        },
                    )

                    MenuItem(
                        leadingIcon = MDIconsSet.DeletePermanent.currentOutlinedPainter,
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
    modifier: Modifier = Modifier,
) {
    val hasSelected by remember(selectedWordsCount) {
        derivedStateOf { selectedWordsCount > 0 }
    }

    val hasNotSelected by remember(totalWordsCount, selectedWordsCount) {
        derivedStateOf { totalWordsCount > selectedWordsCount }
    }
    TopAppBar(
        title = {
            Text("$selectedWordsCount selected of $totalWordsCount") // TODO, string res
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onClearSelection
            ) {
                Icon(
                    painter = MDIconsSet.Close.currentOutlinedPainter,
                    contentDescription = null
                )
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
                        Icon(
                            painter = MDIconsSet.MoreVert.currentOutlinedPainter,
                            contentDescription = null
                        )
                    }
                ) {
                    MenuItem(
                        leadingIcon = MDIconsSet.Delete.currentOutlinedPainter,
                        text = "Delete Selection", // TODO, string res
                        onClick = {
                            dismiss()
                            onDeleteSelection()
                        },
                        enabled = hasSelected,
                        important = true,
                    )
                }
            }
        },
    )
}

@Composable
private fun ColumnScope.MenuItem(
    text: String,
    leadingIcon: Painter,
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
            Icon(leadingIcon, contentDescription = null)
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
                    mutableStateOf(false)
                }
                MDWordsListTopAppBar(
                    isSelectionModeOn = selectionMode,
                    language = Language("ar".code, "العربية", "Arabic"),
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
