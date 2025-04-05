package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIconDropdown
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.util.removePadding
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.label
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDStatisticsTopAppBar(
    onNavigationIconClick: () -> Unit,
    preferences: MDStatisticsViewPreferences,
    availableTrainHistoryCount: Int,
    selectedTrainHistoryCount: MDStatisticsMostResentHistoryCount,
    dateUnit: MDDateUnit?,
    onSelectTrainHistoryCount: (MDStatisticsMostResentHistoryCount) -> Unit,
    onSelectDateUnit: (MDDateUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandDropDownMenu by remember {
        mutableStateOf(false)
    }
    val showActions by remember(preferences) {
        derivedStateOf {
            true
        }
    }
    MDTopAppBar(
        title = {
            Text(eachFirstCapStringResource(R.string.train_statistics))
        },
        modifier = modifier,
        isTopLevel = true,
        onNavigationIconClick = onNavigationIconClick,
        actions = {
            if (showActions) {
                MDIconDropdown(
                    modifier = modifier,
                    menuModifier = Modifier.removePadding(vertical = 8.dp),
                    expanded = expandDropDownMenu,
                    onDismissRequest = {
                        expandDropDownMenu = false
                    },
                    icon = {
                        IconButton(
                            onClick = {
                                expandDropDownMenu = true
                            }
                        ) {
                            MDIcon(MDIconsSet.MoreVert)
                        }
                    }
                ) {
                    PreferencesActionsList(
                        preferences = preferences,
                        availableTrainHistoryCount = availableTrainHistoryCount,
                        selectedTrainHistoryCount = selectedTrainHistoryCount,
                        dateUnit = dateUnit,
                        onSelectTrainHistoryCount = {
                            onSelectTrainHistoryCount(it)
                            expandDropDownMenu = false
                        },
                        onSelectDateUnit = {
                            onSelectDateUnit(it)
                            expandDropDownMenu = false
                        },
                    )
                }
            }
        }
    )
}

@Composable
private fun PreferencesActionsList(
    preferences: MDStatisticsViewPreferences,
    availableTrainHistoryCount: Int,
    selectedTrainHistoryCount: MDStatisticsMostResentHistoryCount,
    dateUnit: MDDateUnit?,
    onSelectTrainHistoryCount: (MDStatisticsMostResentHistoryCount) -> Unit,
    onSelectDateUnit: (MDDateUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (preferences) {
        is MDStatisticsViewPreferences.Train -> {
            MDMostRecentTrainHistoryActions(
                availableTrainHistoryCount = availableTrainHistoryCount,
                selectedTrainHistoryCount = selectedTrainHistoryCount,
                onSelectTrainHistoryCount = onSelectTrainHistoryCount,
                modifier = modifier
            )
        }

        else -> {
            MDDateUnitActions(
                dateUnit = dateUnit,
                onSelectDateUnit = onSelectDateUnit,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun MDMostRecentTrainHistoryActions(
    availableTrainHistoryCount: Int,
    selectedTrainHistoryCount: MDStatisticsMostResentHistoryCount,
    onSelectTrainHistoryCount: (MDStatisticsMostResentHistoryCount) -> Unit,
    modifier: Modifier = Modifier,
) {
    val availableOptions by remember(availableTrainHistoryCount) {
        derivedStateOf {
            MDStatisticsMostResentHistoryCount.entries.filter {
                it.quantity <= availableTrainHistoryCount
            }
        }
    }
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(
                leadingIcon = {
                    MDIcon(MDIconsSet.LatestTrainsCount, contentDescription = null)
                },
                title = "View Preferences" // TODO, string res
            )
        }
    ) {
        availableOptions.forEach { count ->
            MDCard2ListItem(
                onClick = {
                    onSelectTrainHistoryCount(count)
                },
                trailingIcon = selectedTrainHistoryCount.takeIf {
                    it == count
                }?.let {
                    {
                        MDIcon(MDIconsSet.Check, contentDescription = null)
                    }
                },
                title = count.label
            )
        }
    }
}

@Composable
private fun MDDateUnitActions(
    dateUnit: MDDateUnit?,
    onSelectDateUnit: (MDDateUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(
                leadingIcon = {
                    MDIcon(MDIconsSet.TrainHistoryDateGroup, contentDescription = null)
                },
                // TODO, string res
                title = "View Preferences"
            )

        }
    ) {
        MDDateUnit.entries.forEach { unit ->
            MDCard2ListItem(
                onClick = {
                    onSelectDateUnit(unit)
                },
                trailingIcon = unit.takeIf {
                    it == dateUnit
                }?.let {
                    {
                        MDIcon(MDIconsSet.Check, contentDescription = null) //checked
                    }
                },
                title = unit.label
            )
        }
    }
}

@Preview
@Composable
private fun MDStatisticsTopAppBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MDStatisticsTopAppBar(
                        preferences = MDStatisticsViewPreferences.Date(),
                        availableTrainHistoryCount = 0,
                        selectedTrainHistoryCount = MDStatisticsMostResentHistoryCount._1,
                        dateUnit = MDDateUnit.Year,
                        onSelectTrainHistoryCount = {},
                        onSelectDateUnit = {},
                        onNavigationIconClick = {},
                    )
                    MDStatisticsTopAppBar(
                        preferences = MDStatisticsViewPreferences.Train(),
                        availableTrainHistoryCount = 5,
                        selectedTrainHistoryCount = MDStatisticsMostResentHistoryCount._3,
                        dateUnit = MDDateUnit.Year,
                        onSelectTrainHistoryCount = {},
                        onSelectDateUnit = {},
                        onNavigationIconClick = {}
                    )
                }
            }
        }
    }
}