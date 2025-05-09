package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LazyGridCard2
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.progress_indicator.linear.MDLinearProgressIndicator
import dev.bayan_ibrahim.my_dictionary.core.design_system.toAnnotatedString
import dev.bayan_ibrahim.my_dictionary.core.ui.IconSegmentedButton
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2SelectableItem
import dev.bayan_ibrahim.my_dictionary.domain.model.MDTrainQuestionExtraInfo
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainSubmitOption
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordQuestion
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.train.component.MDTrainTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.datetime.Clock

@Composable
fun MDTrainScreen(
    uiState: MDTrainUiState,
    remainingTime: MDTrainWordAnswerTime,
    uiActions: MDTrainUiActions,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(uiState) {
        if (uiState is MDTrainUiState.Finish) {
            uiActions.onNavigateToResultsScreen()
        }
    }
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDTrainTopAppBar(
                uiActions::onPop
            )
        },
    ) {
        val imePadding = WindowInsets.ime
        if (uiState is MDTrainUiState.AnswerWord) {
            val totalCount by remember {
                derivedStateOf {
                    uiState.trainWordsListQuestion.size
                }
            }
            val pagerState = rememberPagerState { totalCount }
            LaunchedEffect(uiState.currentIndex) {
                pagerState.animateScrollToPage(uiState.currentIndex)
            }
            val currentTrainType by remember(uiState) {
                derivedStateOf {
                    uiState.trainWordsListQuestion[uiState.currentIndex].type
                }
            }
            Column(modifier = Modifier) {
                ScreenHeader(
                    currentIndex = uiState.currentIndex,
                    totalCount = totalCount,
                    trainType = currentTrainType.label,
                    remainingTime = remainingTime,
                )
                HorizontalPager(
                    modifier = Modifier,
                    state = pagerState,
                    userScrollEnabled = false,
                ) { i ->
                    WordTrainPage(
                        modifier = Modifier,
                        train = uiState.trainWordsListQuestion[i],
                        onSelectAnswerSubmit = uiActions::onSelectAnswerSubmit,
                        onWriteWordSubmit = uiActions::onWriteWordSubmit,
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(
    currentIndex: Int,
    totalCount: Int,
    trainType: String,
    remainingTime: MDTrainWordAnswerTime,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "${firstCapStringResource(R.string.train_type)}: $trainType",
            style = MaterialTheme.typography.bodyLarge
        )
        MDLinearProgressIndicator(
            progress = currentIndex,
            total = totalCount,
            markLastProgressedItemAsDone = false,
            // active color
            color = MaterialTheme.colorScheme.primary,
            // non active color
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${currentIndex.inc()}/$totalCount",
                modifier = Modifier,
                style = MaterialTheme.typography.labelMedium
            )
            TimerClock(
                modifier = Modifier.size(24.dp),
                remainingTime = remainingTime,
            )
        }
    }
}

@Suppress("InfiniteTransitionLabel", "InfinitePropertiesLabel", "TransitionPropertiesLabel", "UpdateTransitionLabel")
@Composable
private fun TimerClock(
    remainingTime: MDTrainWordAnswerTime,
    modifier: Modifier = Modifier,
    checkIsLowTime: (MDTrainWordAnswerTime) -> Boolean = {
        it.percent <= 0.2
    },
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    filledColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    strokeColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    lowTimeContainerColor: Color = MaterialTheme.colorScheme.errorContainer,
    lowTimeFilledColor: Color = MaterialTheme.colorScheme.onErrorContainer,
    lowTimeStrokeColor: Color = MaterialTheme.colorScheme.onErrorContainer,
    strokeWidth: Dp = 1.dp,
) {
    val percent by animateFloatAsState(
        targetValue = remainingTime.percent.toFloat(),
        label = "time percent"
    )
    val isLowTime = checkIsLowTime(remainingTime)
    val lowTimeTransition = updateTransition(targetState = isLowTime)
    val animatedContainerColor by lowTimeTransition.animateColor {
        if (it) lowTimeContainerColor else containerColor
    }

    val animatedFilledColor by lowTimeTransition.animateColor {
        if (it) lowTimeFilledColor else filledColor
    }

    val animatedStrokeColor by lowTimeTransition.animateColor {
        if (it) lowTimeStrokeColor else strokeColor
    }
    val infiniteTransition = rememberInfiniteTransition()
    val infiniteAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse)
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                this.alpha = (if (isLowTime) infiniteAlpha else 1f)
            }
            .drawBehind {
                drawCircle(color = animatedContainerColor)

                drawArc(
                    color = animatedFilledColor,
                    startAngle = -90f,
                    sweepAngle = 360 * (1 - percent),
                    useCenter = true
                )

                drawCircle(
                    color = animatedStrokeColor,
                    style = Stroke(strokeWidth.toPx())
                )
            }
    )
}

@Composable
private fun WordTrainPage(
    train: MDTrainWordQuestion,
    onSelectAnswerSubmit: (Int, MDTrainSubmitOption) -> Unit,
    onWriteWordSubmit: (String, MDTrainSubmitOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (train) {
        is MDTrainWordQuestion.SelectAnswer -> WordSelectAnswerTrainPage(
            train = train,
            onSubmit = onSelectAnswerSubmit,
            modifier = modifier
        )

        is MDTrainWordQuestion.WriteWord -> WordWriteTrainPage(
            train = train,
            onSubmit = onWriteWordSubmit,
            modifier = modifier
        )
    }
}

@Composable
private fun WordSelectAnswerTrainPage(
    train: MDTrainWordQuestion.SelectAnswer,
    onSubmit: (Int, MDTrainSubmitOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedAnswerIndex: Int by rememberSaveable {
        mutableIntStateOf(-1)
    }
    val selectedAnswer: String by remember(selectedAnswerIndex) {
        derivedStateOf {
            train.options.getOrNull(selectedAnswerIndex) ?: ""
        }
    }
    val onSubmitEnabled by remember(selectedAnswerIndex) {
        derivedStateOf { selectedAnswerIndex >= 0 }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuestionPagePart(
            modifier = Modifier.weight(1f),
            question = train.question,
            currentAnswer = selectedAnswer,
            onSubmit = {
                onSubmit(selectedAnswerIndex, it)
            },
            onSubmitEnabled = onSubmitEnabled
        )
        val columnsCount by remember(train.options.count()) {
            derivedStateOf {
                if (train.options.count() < 4) 1 else 2
            }
        }

        // TODO, pass default selected visible info form view model and store it into it, provide options in profile to provide available extra info
        var selectedVisibleInfo: MDTrainQuestionExtraInfo? by remember {
            mutableStateOf(null)
        }
        ExtraInfoPagePart(
            word = train.word,
            selectedVisibleInfo = selectedVisibleInfo,
            availableExtraInfo = MDTrainQuestionExtraInfo.entries,
            onSelectVisibleInfo = { selectedVisibleInfo = it },
            modifier = Modifier.weight(1f),
        )
        LazyGridCard2(
            columns = GridCells.Fixed(columnsCount),
            contentCount = train.options.count()
        ) { i ->
            val option = train.options[i]
            MDCard2SelectableItem(
                checked = i == selectedAnswerIndex,
                onClick = {
                    selectedAnswerIndex = i
                },
                theme = MDCard2ListItemTheme.SurfaceContainerHighest,
                checkedTheme = MDCard2ListItemTheme.PrimaryContainer,
                leading = {
                    if (i == selectedAnswerIndex) {
                        MDIcon(MDIconsSet.Check)
                    } else {
                        Box(modifier = Modifier.width(24.dp))
                    }
                },
                trailing = {
                    // trailing takes a width to keep the title in the middle
                },
            ) {
                Text(option.toAnnotatedString(), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun WordWriteTrainPage(
    train: MDTrainWordQuestion.WriteWord,
    onSubmit: (String, MDTrainSubmitOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    var answer by remember {
        mutableStateOf("")
    }
    val onSubmitEnabled by remember(answer) {
        derivedStateOf {
            answer.isNotBlank()
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuestionPagePart(
            question = train.question,
            currentAnswer = answer,
            onSubmitEnabled = onSubmitEnabled,
            onSubmit = {
                onSubmit(answer, it)
            },
        )
        var selectedVisibleInfo: MDTrainQuestionExtraInfo? by remember {
            mutableStateOf(null)
        }
        Column {
            MDBasicTextField(
                value = answer,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    answer = it
                },
                placeholder = "Write answer here"
            )
            ExtraInfoPagePart(
                word = train.word,
                selectedVisibleInfo = selectedVisibleInfo,
                availableExtraInfo = MDTrainQuestionExtraInfo.entries,
                onSelectVisibleInfo = { selectedVisibleInfo = it },
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun QuestionPagePart(
    question: String,
    currentAnswer: String,
    onSubmit: (MDTrainSubmitOption) -> Unit,
    modifier: Modifier = Modifier,
    onSubmitEnabled: Boolean = true,
    style: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = question,
                    style = style,
                    modifier = Modifier,
                    textAlign = TextAlign.End
                )
            }
            if (currentAnswer.isBlank()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomStart
                ) {
                    HorizontalDivider(modifier = Modifier.width(100.dp))
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = currentAnswer,
                        style = style,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = TextDecoration.Underline,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDTrainSubmitOption.primaryEntities.forEach { option ->
                AssistChip(
                    onClick = {
                        onSubmit(option)
                    },
                    enabled = onSubmitEnabled,
                    label = {
                        Text(option.label)
                    },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDTrainSubmitOption.secondaryEntities.forEach { option ->
                TextButton(
                    onClick = {
                        onSubmit(option)
                    },
                ) {
                    Text(option.label, textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}

@Composable
private fun ExtraInfoPagePart(
    word: Word,
    selectedVisibleInfo: MDTrainQuestionExtraInfo?,
    availableExtraInfo: List<MDTrainQuestionExtraInfo>,
    onSelectVisibleInfo: (MDTrainQuestionExtraInfo?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val validAvailableExtraInfo by remember(word) {
        derivedStateOf {
            availableExtraInfo.associateWith {
                when (it) {
                    MDTrainQuestionExtraInfo.Transcription -> listOfNotNull(word.transcription.takeUnless { it.isBlank() })
                    MDTrainQuestionExtraInfo.Tag -> word.tags.map { it.value } // TODO, show tags correctly
                    MDTrainQuestionExtraInfo.WordClass -> listOfNotNull(word.wordClass?.name)
                    MDTrainQuestionExtraInfo.RelatedWords -> word.relatedWords.map { it.value }
                    MDTrainQuestionExtraInfo.Example -> word.examples.filter { it.isNotEmpty() }
                    MDTrainQuestionExtraInfo.AdditionalTranslation -> word.additionalTranslations.filter { it.isNotEmpty() }
                }
            }.filterValues { it.isNotEmpty() }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
    ) {
        IconSegmentedButton(
            selected = selectedVisibleInfo,
            allItems = validAvailableExtraInfo.keys,
            onSelectItem = onSelectVisibleInfo
        )
        val selectedVisibleInfoData by remember(selectedVisibleInfo, validAvailableExtraInfo) {
            derivedStateOf {
                validAvailableExtraInfo[selectedVisibleInfo]
            }
        }
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.BottomStart
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = selectedVisibleInfoData != null
            ) {
                ExtraInfoDataAnimatedPart(
                    title = selectedVisibleInfo?.label ?: "",
                    icon = selectedVisibleInfo?.icon ?: MDIconsSet.Close, // TODO, set transparent icon not to show any wrong info on animation
                    valuesItems = selectedVisibleInfoData ?: emptyList(),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExtraInfoDataAnimatedPart(
    title: String,
    icon: MDIconsSet,
    valuesItems: Collection<String>,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier
            .animateContentSize { _, _ -> }
            .width(IntrinsicSize.Max),
        header = {
            MDCard2ListItem(
                title = title,
                leadingIcon = {
                    MDIcon(icon)
                }
            )
        },
    ) {
        FlowRow(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            valuesItems.ifEmpty {
                listOf(firstCapPluralsResource(R.plurals.item, 0))
            }.forEach {
                Text(it)
            }
        }
    }
}

@Preview
@Composable
private fun QuestionPagePartPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuestionPagePart(
                        question = "Question",
                        currentAnswer = "",
                        onSubmit = {}
                    )
                    QuestionPagePart(
                        question = "Question",
                        currentAnswer = "Answer",
                        onSubmit = {}
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExtraInfoPagePartPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center,
            ) {
                var selectedVisibleInfo: MDTrainQuestionExtraInfo? by remember {
                    mutableStateOf(null)
                }
                ExtraInfoPagePart(
                    word = Word(
                        id = 1,
                        meaning = "Meaning",
                        translation = "Translation",
                        additionalTranslations = listOf("Additional translation 1", "Additional translation 2"),
                        transcription = "Transcription",
                        examples = listOf("Example 1", "Example 2"),
                        wordClass = WordClass(
                            1, name = "Type tag", "en".code.getLanguage(),
                            relations = listOf(
                                WordClassRelation("label")
                            ),
                        ),
                        relatedWords = listOf(
                            RelatedWord(0, 0, 0, "label", "related 1"),
                            RelatedWord(0, 0, 0, "label", "related 2"),
                            RelatedWord(0, 0, 0, "label", "related 3"),
                        ),
                        language = "en".code.getLanguage(),
                        createdAt = Clock.System.now(),
                        updatedAt = Clock.System.now(),
                    ),
                    selectedVisibleInfo = selectedVisibleInfo,
                    availableExtraInfo = MDTrainQuestionExtraInfo.entries,
                    onSelectVisibleInfo = { selectedVisibleInfo = it }
                )
            }
        }
    }
}