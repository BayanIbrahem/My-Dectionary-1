package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDialogDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDField
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroupDefaults
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListTrainPreferencesTab
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

@Composable
fun MDWordsListTrainPreferencesDialog(
    state: WordsListTrainPreferencesState,
    actions: WordsListTrainPreferencesActions,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember {
        mutableStateOf(MDWordsListTrainPreferencesTab.TrainType)
    }
    val pagerState = rememberPagerState { MDWordsListTrainPreferencesTab.entries.count() }
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }
    MDAlertDialog(
        showDialog = state.showDialog,
        onDismissRequest = actions::onHideTrainPreferencesDialog,
        modifier = modifier.width(IntrinsicSize.Max),
        headerModifier = Modifier,
        title = {
            MDTabRow(
                tabs = MDWordsListTrainPreferencesTab.entries.map { it.tabData },
                selectedTabIndex = selectedTab.ordinal,
                onClickTab = { i, _ ->
                    selectedTab = MDWordsListTrainPreferencesTab.entries[i]
                },
            )
        },
        actions = {
            MDAlertDialogActions(
                primaryActionLabel = "Train", // TODO string res
                secondaryActionLabel = "Cancel", // TODO string res
                tertiaryActionLabel = "Reset", // TODO string res
                colors = MDDialogDefaults.colors(
                    tertiaryActionColor = MaterialTheme.colorScheme.error
                ),
                onPrimaryClick = actions::onConfirmTrain,
                onSecondaryClick = actions::onHideTrainPreferencesDialog,
                onTertiaryClick = actions::onResetTrainPreferences,
                hasTertiaryAction = true,
            )
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
                    selectedType = state.trainType,
                    selectedTarget = state.trainTarget,
                    onSelectType = actions::onSelectTrainType,
                    onSelectTarget = actions::onSelectTrainTarget,
                )

                MDWordsListTrainPreferencesTab.WordsOrder -> WordsOrderBody(
                    selectedLimit = state.limit,
                    selectedSortBy = state.sortBy,
                    selectedSortByOrder = state.sortByOrder,
                    onSelectLimit = actions::onSelectLimit,
                    onSelectSortBy = actions::onSelectSortBy,
                    onSelectSortByOrder = actions::onSelectSortByOrder,
                )
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
            title = "Train Type",  // TODO, string res
            onClick = onSelectType,
        )

        CheckableGroup(
            data = WordsListTrainTarget.entries,
            selected = selectedTarget,
            title = "Train Target",  // TODO, string res
            onClick = onSelectTarget,
        )
    }
}

@Composable
private fun WordsOrderBody(
    selectedLimit: WordsListTrainPreferencesLimit,
    selectedSortBy: WordsListTrainPreferencesSortBy,
    selectedSortByOrder: WordsListSortByOrder,
    onSelectLimit: (WordsListTrainPreferencesLimit) -> Unit,
    onSelectSortBy: (WordsListTrainPreferencesSortBy) -> Unit,
    onSelectSortByOrder: (WordsListSortByOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    PageColumn(
        modifier = modifier,
    ) {
        MDBasicDropDownMenu(
            value = selectedLimit,
            fieldModifier = Modifier.fillMaxWidth(),
            onValueChange = {},
            fieldReadOnly = true,
            allowCancelSelection = false,
            label = "Words Count limit", // TODO, string res
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
            title = "Sort By", // TODO, string res
            onClick = onSelectSortBy,
        )

        AnimatedVisibility(
            visible = selectedSortBy != WordsListTrainPreferencesSortBy.Random,
        ) {
            CheckableGroup(
                data = WordsListSortByOrder.entries,
                selected = selectedSortByOrder,
                title = "Show first", // TODO, string res
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
    MDFieldsGroup(
        modifier = modifier,
        title = {
            Text(title)
        },
        colors = MDFieldsGroupDefaults.colors(
            fieldColors = MDFieldDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
    ) {
        data.forEachIndexed { i, item ->
            val isSelected by remember(selected) {
                derivedStateOf {
                    selected == item
                }
            }

            val isLast by remember {
                derivedStateOf { i == data.size - 1 }
            }
            CheckableGroupField(
                data = item,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                isSelected = isSelected,
                onClick = onClick,
                getLabel = getLabel,
                isLast = isLast,
            )
        }
    }

}

@Composable
private fun <E> CheckableGroupField(
    data: E,
    isSelected: Boolean,
    contentColor: Color,
    onClick: (E) -> Unit,
    modifier: Modifier = Modifier,
    getLabel: @Composable (E) -> String = { data.label },
    isLast: Boolean = false,
) where E : LabeledEnum, E : IconedEnum {
    val bottomDivider by remember {
        derivedStateOf {
            if (isLast) 0.dp else MDFieldDefaults.horizontalDividerThickness
        }
    }
    MDField(
        bottomHorizontalDividerThickness = bottomDivider,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
            )
        },
        trailingIcon = {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
//                    checkedColor = contentColor,
                    uncheckedColor = contentColor,
                    checkmarkColor = contentColor,
                )
            )
        },
        onClick = {
            onClick(data)
        }
    ) {
        Text(getLabel(data))
    }
}

@Preview
@Composable
private fun MDWordsListFilterDialogPreview() {
    MyDictionaryTheme() {
        val preferences by remember {
            mutableStateOf(WordsListTrainPreferencesMutableState().apply { showDialog = true })
        }
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordsListTrainPreferencesDialog(
                    state = preferences,
                    actions = object : WordsListTrainPreferencesActions {
                        override fun onHideTrainPreferencesDialog() {}
                        override fun onShowTrainPreferencesDialog() {}
                        override fun onSelectTrainType(trainType: TrainWordType) {}
                        override fun onSelectTrainTarget(trainTarget: WordsListTrainTarget) {}
                        override fun onSelectLimit(limit: WordsListTrainPreferencesLimit) {}
                        override fun onSelectSortBy(sortBy: WordsListTrainPreferencesSortBy) {}
                        override fun onSelectSortByOrder(sortByOrder: WordsListSortByOrder) {}
                        override fun onConfirmTrain() {}
                        override fun onResetTrainPreferences() {}
                    }
                )
            }
        }
    }
}