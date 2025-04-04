package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardColors
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CancelAction
import dev.bayan_ibrahim.my_dictionary.core.ui.format
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryLog
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryStatus
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryStepException
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryStepWarning
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.ImportFromFileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFilePartsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFilePicker
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDOptionSelectionGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagExplorerDialog
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun MDImportFromFileScreen(
    uiState: MDImportFromFileUiState,
    summary: MDFileProcessingSummary,
    tagsSelectorUiState: MDTagsSelectorUiState,
    uiActions: MDImportFromFileUiActions,
    tagsSelectorUiActions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    var showTagsExplorerDialog by remember {
        mutableStateOf(false)
    }
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            ImportFromFileTopAppBar(onNavigationIconClick = uiActions::onPop)
        },
        floatingActionButton = {
            AnimatedVisibility(
                uiState.validSelectedFileParts,
            ) {
                ExtendedFloatingActionButton(
                    onClick = uiActions::onStartImportProcess,
                ) {
                    MDIcon(MDIconsSet.ImportFromFile) // TODO, icon res
                    AnimatedVisibility(
                        visible = !scrollState.canScrollForward,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally(),
                    ) {
                        Text(firstCapStringResource(R.string._import))
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MDFilePicker(
                data = uiState.fileData,
                type = uiState.fileType,
                enabled = !uiState.isFetchingAvailablePartsInProgress,
                onPickFile = uiActions::onPickFile,
                onRemove = uiActions::onRemoveFile,
            )
            MDFilePartsSelector(
                selectedParts = uiState.selectedParts,
                visible = uiState.selectedParts.isNotEmpty() && !uiState.isFetchingAvailablePartsInProgress,
                onToggleAvailablePart = uiActions::onToggleSelectAvailablePart
            )
            val primaryColors = MDHorizontalCardDefaults.primaryColors
            MDHorizontalCardGroup {
                item(
                    colors = primaryColors,
                    onClick = {
                        showTagsExplorerDialog = true
                    },
                    subtitle = { Text(firstCapStringResource(R.string.extra_tags_hint)) }
                ) {
                    Text(firstCapStringResource(R.string.extra_tags))
                }
                tagsSelectorUiState.selectedTags.forEach { tag ->
                    item(
                        onLongClick = {
                            tagsSelectorUiActions.onDeleteTag(tag)
                        },
                        leadingIcon = {
                            MDIcon(MDIconsSet.WordTag)
                        }
                    ) {
                        Text(tag.value)
                    }
                }
            }
            val hasSelectedTags by remember {
                derivedStateOf {
                    tagsSelectorUiState.selectedTags.isNotEmpty()
                }
            }
            AnimatedVisibility(
                visible = hasSelectedTags,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                MDOptionSelectionGroup(
                    selectedOption = uiState.extraTagsStrategy,
                    availableOptions = MDExtraTagsStrategy.entries,
                    onSelectOption = uiActions::onChangeExtraTagsStrategy,
                    title = firstCapStringResource(R.string.extra_tags_strategy),
                    subtitle = stringResource(R.string.extra_tags_strategy_hint),
                )
            }
            MDOptionSelectionGroup(
                selectedOption = uiState.corruptedWordStrategy,
                availableOptions = MDPropertyCorruptionStrategy.entries,
                onSelectOption = uiActions::onChangeCorruptedWordStrategy,
                title = firstCapStringResource(R.string.corrupted_word),
                subtitle = firstCapStringResource(R.string.corrupted_word_hint),
            )
            MDOptionSelectionGroup(
                selectedOption = uiState.existedWordStrategy,
                availableOptions = MDPropertyConflictStrategy.entries,
                onSelectOption = uiActions::onChangeExistedWordStrategy,
                title = firstCapStringResource(R.string.existed_word),
                subtitle = firstCapStringResource(R.string.existed_word_hint),
            )
        }
        FileProgressDialog(
            summary = summary,
            onCancel = uiActions::onCancelImportProcess
        )
        MDTagExplorerDialog(
            showDialog = showTagsExplorerDialog,
            onDismissRequest = { showTagsExplorerDialog = false },
            state = tagsSelectorUiState,
            actions = tagsSelectorUiActions
        )
    }
}


@Composable
private fun FileProgressDialog(
    summary: MDFileProcessingSummary,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showDialog by remember(summary.status) {
        derivedStateOf {
            summary.status != MDFileProcessingSummaryStatus.IDLE
        }
    }
    var now by remember {
        mutableStateOf(Clock.System.now())
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = Clock.System.now()
        }
    }
    val passedTime by remember(now, summary.processingStartTime) {
        derivedStateOf {
            summary.processingStartTime.takeIf { it > 0 }?.let {
                now - it.asEpochMillisecondsInstant()
            }
        }
    }
    if (showDialog)
        Dialog(
            onDismissRequest = {},
        ) {
            MDCard2(
                header = {
                    MDCard2ListItem(
                        title = firstCapStringResource(R.string.importing_data),
                        subtitle = passedTime?.format
                    )
                },
                footer = {
                    MDCard2ActionRow {
                        MDCard2CancelAction(onClick = onCancel)
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MDImportProgressStep(currentStep = summary.currentStep)
                    val horizontalPagerState = rememberPagerState { 2 }
                    val scope = rememberCoroutineScope()
                    Column {
                        MDTabRow(
                            tabs = listOf(
                                MDTabData.Label(eachFirstCapPluralsResource(R.plurals.error, summary.exceptions.values.sum())),
                                MDTabData.Label(eachFirstCapPluralsResource(R.plurals.warning, summary.warnings.values.sum())),
                            ),
                            selectedTabIndex = horizontalPagerState.currentPage,
                            onClickTab = { i, _ ->
                                scope.launch {
                                    horizontalPagerState.animateScrollToPage(i)
                                }
                            }
                        )
                        HorizontalPager(
                            state = horizontalPagerState,
                            modifier = Modifier.heightIn(max = 200.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (it == 0) {
                                SummaryExceptions(exceptions = summary.exceptions)
                            } else {
                                SummaryWarnings(warnings = summary.warnings)
                            }
                        }
                    }
                }
            }
        }
}


@Composable
private fun MDImportProgressStep(
    currentStep: MDFileProcessingSummaryActionsStep,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedCurrentStepPager(
            modifier = Modifier.fillMaxWidth(),
            currentStep = currentStep.ordinal,
            stepsCount = MDFileProcessingSummaryActionsStep.entries.count(),
        ) { i ->
            Column(
                modifier = Modifier.widthIn(max = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val step by remember(i) {
                    derivedStateOf {
                        MDFileProcessingSummaryActionsStep.entries[i]
                    }
                }
                MDIcon(step.icon, modifier = Modifier.size(48.dp))
                AnimatedVisibility(
                    visible = step == currentStep,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = step.label,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryExceptions(
    exceptions: Map<MDFileProcessingSummaryStepException, Int>,
    modifier: Modifier = Modifier,
) {
    SummaryLogs(
        logs = exceptions,
        labelPrefix = firstCapStringResource(R.string.error),
        modifier = modifier,
        colors = MDCardDefaults.colors(
            headerContainerColor = MaterialTheme.colorScheme.errorContainer,
            headerContentColor = MaterialTheme.colorScheme.onErrorContainer,
            contentContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    )
}

@Composable
private fun SummaryWarnings(
    warnings: Map<MDFileProcessingSummaryStepWarning, Int>,
    modifier: Modifier = Modifier,
) {
    SummaryLogs(
        logs = warnings,
        labelPrefix = firstCapStringResource(R.string.warning),
        modifier = modifier,
        colors = MDCardDefaults.colors(
            headerContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            headerContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            contentContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    )
}

@Composable
private fun SummaryLogs(
    logs: Map<out MDFileProcessingSummaryLog, Int>,
    labelPrefix: String,
    modifier: Modifier = Modifier,
    colors: MDCardColors = MDCardDefaults.colors(),
) {
    val logsList by remember(logs) {
        derivedStateOf {
            logs.toList()
        }
    }
    var expanded: MDFileProcessingSummaryLog? by remember {
        mutableStateOf(null)
    }
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logsList) { (log, duplications) ->
            val duplicationsSuffix by remember(duplications) {
                derivedStateOf {
                    if (duplications <= 1) "" else " x$duplications"
                }
            }
            val currentExpanded by remember(expanded, log) {
                derivedStateOf { expanded == log }
            }
            MDCard2(
                modifier = Modifier.animateItem(),
                header = {
                    MDCard2ListItem(
                        title = "$labelPrefix$duplicationsSuffix ${log.label}",
                        leadingIcon = {
                            MDIcon(MDIconsSet.Close) // TODO, icon res
                        },
                        onClick = {
                            expanded = if (expanded == log) {
                                null
                            } else {
                                log
                            }
                        },
                    )
                }
            ) {
                AnimatedVisibility(
                    visible = currentExpanded
                ) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = firstCapStringResource(R.string.what_went_wrong),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = log.cause,
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = firstCapStringResource(R.string.possible_solutions),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        log.suggestions.forEach { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedCurrentStepPager(
    currentStep: Int,
    stepsCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable (step: Int) -> Unit,
) {
    val stepAnimatable by remember {
        derivedStateOf { Animatable(currentStep.toFloat()) }
    }
    LaunchedEffect(currentStep) {
        stepAnimatable.animateTo(currentStep.toFloat())
    }
    val animatedCurrentStep by stepAnimatable.asState()
    AnimatedCurrentStepLayout(
        modifier = modifier,
        animatedStep = animatedCurrentStep,
        content = @Composable {
            repeat(stepsCount) { i ->
                val scale by remember(animatedCurrentStep) {
                    derivedStateOf {
                        val distance = abs(i - animatedCurrentStep).coerceIn(0f, 1f)
                        1f - distance / 2
                    }
                }
                Box(
                    modifier = Modifier.graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = scale
                    },
                ) {
                    content(i)
                }
            }
        }
    )
}

@Composable
private fun AnimatedCurrentStepLayout(
    animatedStep: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constrains ->
        val width = constrains.maxWidth

        val placeables = measurables.map { it.measure(constrains.copy(minWidth = 0)) }
        val height = placeables.maxOf { it.height }

        layout(width, height) {
            placeables.forEachIndexed { i, p ->
                val distance = i - animatedStep
                val x = calculateOutput(
                    input = distance,
                    inputRangeStart = -1f,
                    inputRangeEnd = 1f,
                    outputRangeStart = p.width / 2f,
                    outputRangeEnd = (width - p.width / 2f),
                ).roundToInt().minus(p.width / 2)
                val y = (height - p.height) / 2
                p.placeRelative(x = x, y = y)
            }
        }
    }
}

@Preview
@Composable
private fun MDImportProgressStepPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var currentIndex: Int by remember {
                mutableIntStateOf(0)
            }
            val currentStep: MDFileProcessingSummaryActionsStep by remember(currentIndex) {
                derivedStateOf {
                    MDFileProcessingSummaryActionsStep.entries[currentIndex]
                }
            }
            LaunchedEffect(Unit) {
                repeat(MDFileProcessingSummaryActionsStep.entries.count()) {
                    delay(5.seconds)
                    currentIndex = it
                }

            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDImportProgressStep(currentStep = currentStep)
            }
        }
    }
}