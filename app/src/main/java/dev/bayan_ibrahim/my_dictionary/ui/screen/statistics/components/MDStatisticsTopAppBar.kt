package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicIconDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.util.removePadding
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.date.label
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDStatisticsTopAppBar(
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
    TopAppBar(
        title = {
            Text("Train Statistics")
        },
        modifier = modifier,
        actions = {
            if (showActions) {
                MDBasicIconDropDownMenu(
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
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                    }
                ) {
                    PreferencesActionsList(
                        preferences = preferences,
                        availableTrainHistoryCount = availableTrainHistoryCount,
                        selectedTrainHistoryCount = selectedTrainHistoryCount,
                        dateUnit = dateUnit,
                        onSelectTrainHistoryCount = onSelectTrainHistoryCount,
                        onSelectDateUnit = onSelectDateUnit,
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
                it.count <= availableTrainHistoryCount
            }
        }
    }
    MDHorizontalCardGroup(
        modifier = modifier,
    ) {
        item(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add, // TODO, icon res
                    contentDescription = null
                )
            }
        ) {
            Text("View Preferences")
        }
        availableOptions.forEach { count ->
            item(
                onClick = {
                    onSelectTrainHistoryCount(count)
                },
                trailingIcon = selectedTrainHistoryCount.takeIf {
                    it == count
                }?.let {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            ) {
                Text(count.label)
            }
        }
    }
}

@Composable
private fun MDDateUnitActions(
    dateUnit: MDDateUnit?,
    onSelectDateUnit: (MDDateUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDHorizontalCardGroup(
        modifier = modifier,
    ) {
        item(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange, // TODO, icon res
                    contentDescription = null
                )
            }
        ) {
            Text("View Preferences")
        }
        MDDateUnit.entries.forEach { unit ->
            item(
                onClick = {
                    onSelectDateUnit(unit)
                },
                trailingIcon = unit.takeIf { it == dateUnit }?.let {
                    {
                        Icon(Icons.Default.Check, null)
                    }
                }
            ) {
                Text(unit.label)
            }
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
                        onSelectDateUnit = {}
                    )
                    MDStatisticsTopAppBar(
                        preferences = MDStatisticsViewPreferences.Train(),
                        availableTrainHistoryCount = 5,
                        selectedTrainHistoryCount = MDStatisticsMostResentHistoryCount._3,
                        dateUnit = MDDateUnit.Year,
                        onSelectTrainHistoryCount = {},
                        onSelectDateUnit = {}
                    )
                }
            }
        }
    }
}