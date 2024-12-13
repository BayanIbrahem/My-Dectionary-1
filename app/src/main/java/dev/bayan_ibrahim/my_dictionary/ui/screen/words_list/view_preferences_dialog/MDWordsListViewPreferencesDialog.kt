package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
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
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.checkboxItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.radioItem
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListLearningProgressGroup
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
        MDWordFieldTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.fillMaxWidth(),
            showLabelOnEditMode = true,
            label = "Search Query", // TODO, string res
            placeholder = "Eg. Car" // TODO, string res
        )
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
    val colors = MDHorizontalCardDefaults.primaryColors
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
        MDHorizontalCardGroup {
            checkboxItem(
                checked = includeSelectedTags,
                colors = colors,
                onClick = {
                    onToggleSelectedTags(!includeSelectedTags)
                },
                subtitle = {
                    // TODO, string res
                    val text = if (includeSelectedTags) {
                        "any result should contains at least one of the selected tags"
                    } else {
                        "all results should NOT contain any one of the selected tags"
                    }
                    Text(
                        text = text,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                    )
                },
            ) {
                Text(text = "Include Selected Tabs")
            }
        }

        MDHorizontalCardGroup(
            title = { Text("Learning groups") }
        ) {
            MDWordsListLearningProgressGroup.entries.forEach { group ->
                checkboxItem(
                    checked = group in selectedLearningProgressGroups,
                    colors = colors,
                    onClick = {
                        onSelectLearningGroup(group)
                    },
                    subtitle = {
                        // TODO, string res
                        val text = "from ${
                            group.learningRange.start.times(100).roundToInt()
                        }% to ${
                            group.learningRange.endInclusive.times(100).roundToInt()
                        }%"
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
    Column(modifier = modifier.fillMaxWidth()) {
        MDHorizontalCardGroup(
            // TODO, string res
            title = { Text("Sorted By") }
        ) {
            MDWordsListViewPreferencesSortBy.entries.forEach { sortBy ->
                radioItem(
                    selected = sortBy == selectedSortBy,
                    colors = colors,
                    onClick = { onSelectSortBy(sortBy) },
                ) {
                    Text(text = sortBy.label)
                }
            }
        }

        MDHorizontalCardGroup(
            // TODO, string res
            title = { Text("Sorted By Order") }
        ) {
            MDWordsListSortByOrder.entries.forEach { sortByOrder ->
                radioItem(
                    colors = colors,
                    selected = sortByOrder == selectedSortByOrder,
                    onClick = { onSelectSortByOrder(sortByOrder) },
                ) {
                    Text(text = sortByOrder.label)
                }
            }
        }
    }
}
