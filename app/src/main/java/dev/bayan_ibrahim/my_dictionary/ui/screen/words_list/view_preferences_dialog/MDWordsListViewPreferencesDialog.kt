package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CheckboxItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ImportantAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2RadioButtonItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagExplorerDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagListItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesTab
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlin.math.roundToInt

@Composable
fun MDWordsListViewPreferencesDialog(
    showDialog: Boolean,
    uiState: MDWordsListViewPreferencesUiState,
    uiActions: MDWordsListViewPreferencesUiActions,
    tagsSelectionState: MDTagsSelectorUiState,
    tagsSelectionActions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember {
        mutableStateOf(MDWordsListViewPreferencesTab.Search)
    }
    val pagerState = rememberPagerState { MDWordsListViewPreferencesTab.entries.count() }
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }
    if (showDialog)
        Dialog(
            onDismissRequest = uiActions::onDismissDialog,
        ) {
            MDCard2(
                modifier = modifier,
                cardModifier = Modifier.width(IntrinsicSize.Max),
                header = {
                    MDTabRow(
                        tabs = MDWordsListViewPreferencesTab.entries.map { it.tabData },
                        selectedTabIndex = selectedTab.ordinal,
                        onClickTab = { i, _ ->
                            selectedTab = MDWordsListViewPreferencesTab.entries[i]
                        },
                    )
                },
                footer = {
                    MDCard2ActionRow {
                        MDCard2ImportantAction(
                            label = firstCapStringResource(R.string.reset),
                        ) {
                            uiActions.onClearViewPreferences()
                            uiActions.onDismissDialog()
                        }
                    }
                }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.size(300.dp, 300.dp),
                    contentPadding = PaddingValues(8.dp),
                    userScrollEnabled = false,
                    pageSpacing = 8.dp,
                    verticalAlignment = Alignment.Top,
                ) { i ->
                    when (MDWordsListViewPreferencesTab.entries[i]) {
                        MDWordsListViewPreferencesTab.Search -> SearchBody(
                            searchQuery = uiState.searchQuery,
                            onSearchQueryChange = uiActions::onSearchQueryChange,
                            selectedSearchTarget = uiState.searchTarget,
                            onSelectSearchTarget = uiActions::onSelectSearchTarget,
                        )

                        MDWordsListViewPreferencesTab.Filter -> FilterBody(
                            tagsSelectionState = tagsSelectionState,
                            tagsSelectionActions = tagsSelectionActions,
                            includeSelectedTags = uiState.includeSelectedTags,
                            selectedMemorizingProbabilityGroups = uiState.selectedMemorizingProbabilityGroups,
                            onToggleSelectedTags = uiActions::onToggleIncludeSelectedTags,
                            onSelectLearningGroup = uiActions::onSelectLearningGroup,
                        )

                        MDWordsListViewPreferencesTab.Sort -> SortBody(
                            selectedSortBy = uiState.sortBy,
                            selectedSortByOrder = uiState.sortByOrder,
                            onSelectSortBy = uiActions::onSelectWordsSortBy,
                            onSelectSortByOrder = uiActions::onSelectWordsSortByOrder,
                        )
                    }
                }
            }
        }
}

@Composable
private fun SearchBody(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSearchTarget: MDWordsListSearchTarget,
    onSelectSearchTarget: (MDWordsListSearchTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier,
    ) {
        val selectedTheme = MDCard2ListItemTheme.PrimaryOnSurface.onSurfaceHighest
        MDWordsListSearchTarget.entries.forEach { target ->
            MDCard2RadioButtonItem(
                selected = target == selectedSearchTarget,
                theme = MDCard2ListItemTheme.SurfaceContainerHighest,
                selectedTheme = selectedTheme,
                onClick = { onSelectSearchTarget(target) },
                title = target.label
            )
        }
    }
}

@Composable
private fun FilterBody(
    tagsSelectionState: MDTagsSelectorUiState,
    tagsSelectionActions: MDTagsSelectorUiActions,
    includeSelectedTags: Boolean,
    selectedMemorizingProbabilityGroups: Set<MDWordsListMemorizingProbabilityGroup>,
    onToggleSelectedTags: (Boolean) -> Unit,
    onSelectLearningGroup: (MDWordsListMemorizingProbabilityGroup) -> Unit,
    theme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceContainerHighest,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        var showTagExploreDialog by remember {
            mutableStateOf(false)
        }
        val selectedTheme = MDCard2ListItemTheme.PrimaryOnSurface.onSurfaceHighest
        MDCard2(
            header = {
                MDCard2ListItem(
                    title = firstCapStringResource(R.string.tags),
                    subtitle = if (tagsSelectionState.selectedTags.isEmpty()) {
                        null
                    } else if (includeSelectedTags) {
                        firstCapStringResource(R.string.include_selected_tags_hint_on)
                    } else {
                        firstCapStringResource(R.string.include_selected_tags_hint_off)
                    },
                    subtitleMaxLines = 2,
                    leadingIcon = {
                        MDIcon(MDIconsSet.WordTag)
                    },
                    trailingIcon = {
                        MDIcon(MDIconsSet.Add)
                    },
                    onTrailingClick = {
                        showTagExploreDialog = true
                    }
                )
            },
            contentTheme = theme
        ) {
            AnimatedVisibility(
                visible = tagsSelectionState.selectedTags.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                MDCard2CheckboxItem(
                    checked = includeSelectedTags,
                    theme = theme,
                    checkedTheme = selectedTheme,
                    onCheckedChange = {
                        onToggleSelectedTags(!includeSelectedTags)
                    },
                    title = firstCapStringResource(R.string.include_selected_tags)
                )
            }
            tagsSelectionState.selectedTags.forEachIndexed { i, tag ->
                MDTagListItem(
                    tag = tag,
                    onTrailingIconClick = {
                        tagsSelectionActions.onUnSelectTag(tag)
                    },
                    tagOrder = i.inc()
                )
            }
            MDTagExplorerDialog(
                showDialog = showTagExploreDialog,
                onDismissRequest = { showTagExploreDialog = false },
                state = tagsSelectionState,
                actions = tagsSelectionActions,
                allowAddTags = false,
                allowRemoveTags = false,
            )
        }

        MDCard2(
            overline = { MDCard2Overline(firstCapStringResource(R.string.learning_groups)) }
        ) {
            MDWordsListMemorizingProbabilityGroup.entries.forEach { group ->
                MDCard2CheckboxItem(
                    checked = group in selectedMemorizingProbabilityGroups,
                    theme = theme,
                    checkedTheme = selectedTheme,
                    onCheckedChange = {
                        onSelectLearningGroup(group)
                    },
                    subtitle = stringResource(
                        R.string.from_x_to_y,
                        group.probabilityRange.start.times(100).roundToInt().toString(),
                        group.probabilityRange.endInclusive.times(100).roundToInt().toString()
                    ),
                    title = group.label
                )
            }
        }
    }
}

@Composable
private fun SortBody(
    selectedSortBy: MDWordsListViewPreferencesSortBy,
    selectedSortByOrder: MDWordsListSortByOrder,
    onSelectSortBy: (MDWordsListViewPreferencesSortBy) -> Unit,
    onSelectSortByOrder: (MDWordsListSortByOrder) -> Unit,
    theme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceContainerHighest,
    modifier: Modifier = Modifier,
) {
    val selectedTheme = MDCard2ListItemTheme.PrimaryOnSurface.onSurfaceHighest
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        MDCard2(
            overline = { MDCard2Overline(firstCapStringResource(R.string.sorted_by)) }
        ) {
            MDWordsListViewPreferencesSortBy.entries.forEach { sortBy ->
                MDCard2RadioButtonItem(
                    selected = sortBy == selectedSortBy,
                    theme = theme,
                    selectedTheme = selectedTheme,
                    onClick = { onSelectSortBy(sortBy) },
                    secondary = {
                        // item has a label so no need for content description
                        MDIcon(icon = sortBy.icon, contentDescription = null)
                    },
                    leadingRadioButton = true,
                    title = sortBy.label
                )
            }
        }

        MDCard2(
            overline = { MDCard2Overline(firstCapStringResource(R.string.sorted_by_order)) }
        ) {
            MDWordsListSortByOrder.entries.forEach { sortByOrder ->
                MDCard2RadioButtonItem(
                    theme = theme,
                    selected = sortByOrder == selectedSortByOrder,
                    selectedTheme = selectedTheme,
                    onClick = { onSelectSortByOrder(sortByOrder) },
                    secondary = {
                        MDIcon(icon = sortByOrder.icon, contentDescription = null)
                    },
                    title = sortByOrder.label
                )
            }
        }
    }
}
