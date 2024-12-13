package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesTab
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListLearningProgressGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlin.math.roundToInt

@Composable
fun MDWordsListViewPreferencesDialog(
    showDialog: Boolean,
    uiState: MDWordsListViewPreferencesUiState,
    uiActions: MDWordsListViewPreferencesUiActions,
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
                    selectedTags = uiState.selectedTags,
                    tagSearchQuery = uiState.tagSearchQuery,
                    tagsSuggestions = uiState.tagsSuggestions,
                    includeSelectedTags = uiState.includeSelectedTags,
                    selectedLearningProgressGroups = uiState.selectedLearningProgressGroups,
                    onTagSearchQueryChange = uiActions::onTagSearchQueryChange,
                    onSelectTag = uiActions::onSelectTag,
                    onRemoveTag = uiActions::onRemoveTag,
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
/*
    val searchQuery: String
    val searchTarget: WordsListSearchTarget
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchBody(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSearchTarget: MDWordsListSearchTarget,
    onSelectSearchTarget: (MDWordsListSearchTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MDWordFieldTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.fillMaxWidth(),
            showLabelOnEditMode = true,
            label = "Search Query", // TODO, string res
            placeholder = "Eg. Car" // TODO, string res
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MDWordsListSearchTarget.entries.forEach { target ->
                CheckableListItem(
                    headline = target.label,
                    onClick = {
                        onSelectSearchTarget(target)
                    }
                ) {
                    RadioButton(selected = target == selectedSearchTarget, onClick = null)
                }
            }
        }
    }
}

/*


    val selectedTags: Set<String>
    val includeSelectedTags: Boolean
    val selectedLearningProgressGroups: Set<WordsListLearningProgressGroup>
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterBody(
    selectedTags: Set<String>,
    tagSearchQuery: String,
    tagsSuggestions: List<String>,
    includeSelectedTags: Boolean,
    selectedLearningProgressGroups: Set<MDWordsListLearningProgressGroup>,
    onTagSearchQueryChange: (String) -> Unit,
    onSelectTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onToggleSelectedTags: (Boolean) -> Unit,
    onSelectLearningGroup: (MDWordsListLearningProgressGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MDWordFieldTextField(
            value = tagSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onTagSearchQueryChange,
            suggestions = tagsSuggestions,
            onSelectSuggestion = { _, tag ->
                tag?.let(onSelectTag)
            },
            allowCancelSelection = false,
            suggestionTitle = { this },
            leadingIcon = Icons.Default.Add, // TODO, icon res
            label = "Tag Query", // TODO, string res
            placeholder = "Eg. Food" // TODO, string res
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            selectedTags.forEach { tag ->
                AssistChip(
                    onClick = { onRemoveTag(tag) },
                    label = { Text(tag) },
                    shape = MaterialTheme.shapes.medium,
                )
            }

        }
        CheckableListItem(
            headline = "Include Selected Tabs",
            supportingText = if (includeSelectedTags) {
                // TODO, string res
                "any result should contains at least one of the selected tags"
            } else {
                // TODO, string res
                "all results should NOT contain any one of the selected tags"
            },
            onClick = {
                onToggleSelectedTags(!includeSelectedTags)
            }
        ) {
            Checkbox(checked = includeSelectedTags, onCheckedChange = null)
        }
        Text("Learning groups", style = MaterialTheme.typography.labelLarge) // TODO, string res
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MDWordsListLearningProgressGroup.entries.forEach { group ->
                CheckableListItem(
                    headline = group.label,
                    supportingText = "from ${
                        group.learningRange.start.times(100).roundToInt()
                    }% to ${
                        group.learningRange.endInclusive.times(100).roundToInt()
                    }%",// TODO, string res
                    onClick = {
                        onSelectLearningGroup(group)
                    }
                ) {
                    Checkbox(checked = group in selectedLearningProgressGroups, onCheckedChange = null)
                }
            }
        }
    }
}

/*
    val sortBy: WordsListSortBy
    val sortByOrder: WordsListSortByOrder
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SortBody(
    selectedSortBy: MDWordsListViewPreferencesSortBy,
    selectedSortByOrder: MDWordsListSortByOrder,
    onSelectSortBy: (MDWordsListViewPreferencesSortBy) -> Unit,
    onSelectSortByOrder: (MDWordsListSortByOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Sorted By", style = MaterialTheme.typography.labelLarge) // TODO, string res
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MDWordsListViewPreferencesSortBy.entries.forEach { sortBy ->
                CheckableListItem(
                    modifier = Modifier,
                    headline = sortBy.label,
                    onClick = { onSelectSortBy(sortBy) }
                ) {
                    RadioButton(selected = sortBy == selectedSortBy, onClick = null)
                }
            }
        }
        Text("Sorted By Order", style = MaterialTheme.typography.labelLarge) // TODO, string res
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MDWordsListSortByOrder.entries.forEach { sortByOrder ->
                CheckableListItem(
                    headline = sortByOrder.label,
                    onClick = { onSelectSortByOrder(sortByOrder) }
                ) {
                    RadioButton(selected = sortByOrder == selectedSortByOrder, onClick = null)
                }
            }
        }
    }
}

@Composable
private fun CheckableListItem(
    headline: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        Column {
            Text(text = headline, style = MaterialTheme.typography.bodyMedium)
            supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}