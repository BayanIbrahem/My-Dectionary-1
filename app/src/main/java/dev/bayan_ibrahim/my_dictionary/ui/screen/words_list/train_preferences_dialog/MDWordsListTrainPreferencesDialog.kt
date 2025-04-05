package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CancelAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ConfirmAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ImportantAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2RadioButtonItem
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListTrainPreferencesTab
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

@Composable
fun MDWordsListTrainPreferencesDialog(
    showDialog: Boolean,
    uiState: MDWordsListTrainPreferencesUiState,
    uiActions: MDWordsListTrainPreferencesUiActions,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember {
        mutableStateOf(MDWordsListTrainPreferencesTab.TrainType)
    }
    val pagerState = rememberPagerState { MDWordsListTrainPreferencesTab.entries.count() }
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }
    if (showDialog)
        Dialog(
            onDismissRequest = uiActions::onDismissDialog,
        ) {
            MDCard2(
                modifier = modifier,
                header = {
                    MDTabRow(
                        tabs = MDWordsListTrainPreferencesTab.entries.map { it.tabData },
                        selectedTabIndex = selectedTab.ordinal,
                        onClickTab = { i, _ ->
                            selectedTab = MDWordsListTrainPreferencesTab.entries[i]
                        },
                    )
                },
                footer = {
                    MDCard2ActionRow {
                        MDCard2ConfirmAction(
                            label = firstCapStringResource(R.string.train),
                            onClick = uiActions::onConfirmTrain,
                        )

                        MDCard2CancelAction(
                            onClick = uiActions::onDismissDialog,
                        )

                        MDCard2ImportantAction(
                            label = firstCapStringResource(R.string.reset),
                            onClick = uiActions::onResetTrainPreferences,
                        )
                    }
                }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.size(350.dp, 350.dp),
                    contentPadding = PaddingValues(8.dp),
                    userScrollEnabled = false,
                    pageSpacing = 8.dp,
                    verticalAlignment = Alignment.Top,
                ) { i ->
                    when (MDWordsListTrainPreferencesTab.entries[i]) {
                        MDWordsListTrainPreferencesTab.TrainType -> TrainTypeBody(
                            selectedType = uiState.trainType,
                            selectedTarget = uiState.trainTarget,
                            onSelectType = uiActions::onSelectTrainType,
                            onSelectTarget = uiActions::onSelectTrainTarget,
                        )

                        MDWordsListTrainPreferencesTab.WordsOrder -> WordsOrderBody(
                            selectedLimit = uiState.limit,
                            selectedSortBy = uiState.sortBy,
                            selectedSortByOrder = uiState.sortByOrder,
                            onSelectLimit = uiActions::onSelectLimit,
                            onSelectSortBy = uiActions::onSelectSortBy,
                            onSelectSortByOrder = uiActions::onSelectSortByOrder,
                        )
                    }
                }
            }
        }
}

@Composable
private fun TrainTypeBody(
    selectedType: TrainWordType,
    selectedTarget: WordsListTrainTarget,
    onSelectType: (TrainWordType) -> Unit,
    onSelectTarget: (WordsListTrainTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    PageColumn(
        modifier = modifier
    ) {
        CheckableGroup(
            data = TrainWordType.entries,
            selected = selectedType,
            title = firstCapStringResource(R.string.train_type),
            onClick = onSelectType,
        )

        CheckableGroup(
            data = WordsListTrainTarget.entries,
            selected = selectedTarget,
            title = firstCapStringResource(R.string.train_target),
            onClick = onSelectTarget,
        )
    }
}

@Composable
private fun WordsOrderBody(
    selectedLimit: WordsListTrainPreferencesLimit,
    selectedSortBy: WordsListTrainPreferencesSortBy,
    selectedSortByOrder: MDWordsListSortByOrder,
    onSelectLimit: (WordsListTrainPreferencesLimit) -> Unit,
    onSelectSortBy: (WordsListTrainPreferencesSortBy) -> Unit,
    onSelectSortByOrder: (MDWordsListSortByOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    PageColumn(
        modifier = modifier,
    ) {
        MDBasicDropDownMenu(
            modifier = Modifier,
            value = selectedLimit,
            fieldModifier = Modifier,
            onValueChange = {},
            fieldReadOnly = true,
            menuMatchFieldWidth = false,
            allowCancelSelection = false,
            label = firstCapStringResource(R.string.words_count_limit),
            onSelectSuggestion = { i, limit ->
                limit?.let(onSelectLimit)
            },
            suggestions = WordsListTrainPreferencesLimit.entries,
            suggestionTitle = {
                this.label
            }
        )
        CheckableGroup(
            data = WordsListTrainPreferencesSortBy.entries,
            selected = selectedSortBy,
            title = firstCapStringResource(R.string.sort_by),
            onClick = onSelectSortBy,
        )

        AnimatedVisibility(
            visible = selectedSortBy != WordsListTrainPreferencesSortBy.Random,
        ) {
            CheckableGroup(
                data = MDWordsListSortByOrder.entries,
                selected = selectedSortByOrder,
                title = firstCapStringResource(R.string.show_first),
                onClick = onSelectSortByOrder,
                getLabel = {
                    selectedSortBy.orderLabel(it)
                }
            )
        }
    }
}

@Composable
private fun PageColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

@Composable
private fun <E> CheckableGroup(
    data: Collection<E>,
    selected: E?,
    title: String,
    onClick: (E) -> Unit,
    modifier: Modifier = Modifier,
    getLabel: @Composable (E) -> String = { it.label },
) where E : LabeledEnum, E : IconedEnum {
    MDCard2(
        modifier = modifier,
        overline = {
            MDCard2Overline(title)
        },
    ) {
        data.forEach { item ->
            MDCard2RadioButtonItem(
                selected == item,
                onClick = {
                    onClick(item)
                },
                theme = MDCard2ListItemTheme.PrimaryOnSurface,
                secondary = {
                    MDIcon(
                        icon = item.icon,
                        outline = item.outline,
                        contentDescription = null // the checkable group has a label itself
                    )
                },
                leadingRadioButton = false,
            ) {
                Text(getLabel(item))
            }
        }
    }

}

@Preview
@Composable
private fun MDWordsListFilterDialogPreview() {
    MyDictionaryTheme() {
        val preferences by remember {
            mutableStateOf(MDWordsListTrainPreferencesMutableUiState())
        }
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordsListTrainPreferencesDialog(
                    showDialog = true,
                    uiState = preferences,
                    uiActions = object : MDWordsListTrainPreferencesBusinessUiActions, MDWordsListTrainPreferencesNavigationUiActions {
                        override fun onSelectTrainType(trainType: TrainWordType) {}
                        override fun onSelectTrainTarget(trainTarget: WordsListTrainTarget) {}
                        override fun onSelectLimit(limit: WordsListTrainPreferencesLimit) {}
                        override fun onSelectSortBy(sortBy: WordsListTrainPreferencesSortBy) {}
                        override fun onSelectSortByOrder(sortByOrder: MDWordsListSortByOrder) {}
                        override fun onConfirmTrain() {}
                        override fun onResetTrainPreferences() {}
                        override fun onDismissDialog() {}
                        override fun navigateToTrainScreen() {}
                    }.let {
                        MDWordsListTrainPreferencesUiActions(it, it)
                    }
                )
            }
        }
    }
}