package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.checkboxItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.radioItem
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesTab
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
    MDBasicDialog(
        showDialog = showDialog,
        onDismissRequest = uiActions::onDismissDialog,
        modifier = modifier.widthIn(max = 325.dp),
        headerModifier = Modifier,
        title = {
            MDTabRow(
                tabs = MDWordsListViewPreferencesTab.entries.map { it.tabData },
                selectedTabIndex = selectedTab.ordinal,
                onClickTab = { i, _ ->
                    selectedTab = MDWordsListViewPreferencesTab.entries[i]
                },
            )
        },

        actions = {
            MDAlertDialogActions(
                onDismissRequest = uiActions::onDismissDialog,
                hasPrimaryAction = false,
                hasSecondaryAction = false,
                hasTertiaryAction = true,
                tertiaryActionLabel = "Reset",
                onTertiaryClick = uiActions::onClearViewPreferences

            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.height(300.dp),
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

@Composable
private fun SearchBody(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSearchTarget: MDWordsListSearchTarget,
    onSelectSearchTarget: (MDWordsListSearchTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MDHorizontalCardDefaults.primaryColors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MDHorizontalCardGroup {
            MDWordsListSearchTarget.entries.forEach { target ->
                radioItem(
                    selected = target == selectedSearchTarget,
                    colors = colors,
                    onClick = { onSelectSearchTarget(target) }
                ) {
                    Text(target.label)
                }
            }
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
    modifier: Modifier = Modifier,
) {
    val colors = MDHorizontalCardDefaults.primaryColors
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MDTagsSelector(
            state = tagsSelectionState,
            actions = tagsSelectionActions,
            allowAddTags = false,
            allowRemoveTags = false,
            allowEditTags = true,
        )
        MDHorizontalCardGroup {
            checkboxItem(
                checked = includeSelectedTags,
                colors = colors,
                onClick = {
                    onToggleSelectedTags(!includeSelectedTags)
                },
                subtitle = {
                    val text = if (includeSelectedTags) {
                        firstCapStringResource(R.string.include_selected_tags_hint_on)
                    } else {
                        firstCapStringResource(R.string.include_selected_tags_hint_off)
                    }
                    Text(
                        text = text,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                    )
                },
            ) {
                Text(text = firstCapStringResource(R.string.include_selected_tags))
            }
        }

        MDHorizontalCardGroup(
            title = { Text(firstCapStringResource(R.string.learning_groups)) }
        ) {
            MDWordsListMemorizingProbabilityGroup.entries.forEach { group ->
                checkboxItem(
                    checked = group in selectedMemorizingProbabilityGroups,
                    colors = colors,
                    onClick = {
                        onSelectLearningGroup(group)
                    },
                    subtitle = {
                        val text = stringResource(
                            R.string.from_x_to_y,
                            group.probabilityRange.start.times(100).roundToInt(),
                            group.probabilityRange.endInclusive.times(100).roundToInt()
                        )
                        Text(
                            text = text,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                        )
                    },
                ) {
                    Text(text = group.label)
                }
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
    modifier: Modifier = Modifier,
) {
    val colors = MDHorizontalCardDefaults.primaryColors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        MDHorizontalCardGroup(
            title = { Text(firstCapStringResource(R.string.sorted_by)) }
        ) {
            MDWordsListViewPreferencesSortBy.entries.forEach { sortBy ->
                radioItem(
                    selected = sortBy == selectedSortBy,
                    colors = colors,
                    onClick = { onSelectSortBy(sortBy) },
                    trailingIcon = {
                        // item has a label so no need for content description
                        MDIcon(icon = sortBy.icon, contentDescription = null)
                    }
                ) {
                    Text(text = sortBy.label)
                }
            }
        }

        MDHorizontalCardGroup(
            title = { Text(firstCapStringResource(R.string.sorted_by_order)) }
        ) {
            MDWordsListSortByOrder.entries.forEach { sortByOrder ->
                radioItem(
                    colors = colors,
                    selected = sortByOrder == selectedSortByOrder,
                    onClick = { onSelectSortByOrder(sortByOrder) },
                    trailingIcon = {
                        // item has a label so no need for content description
                        MDIcon(icon = sortByOrder.icon, contentDescription = null)
                    }
                ) {
                    Text(text = sortByOrder.label)
                }
            }
        }
    }
}
