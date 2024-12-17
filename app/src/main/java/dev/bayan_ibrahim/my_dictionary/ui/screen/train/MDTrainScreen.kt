package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import androidx.compose.animation.animateColor
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.progress_indicator.linear.MDLinearProgressIndicator
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordQuestion
import dev.bayan_ibrahim.my_dictionary.ui.screen.train.component.MDTrainTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

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
            MDTrainTopAppBar()
        },
    ) {
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
            Column {
                ScreenHeader(
                    currentIndex = uiState.currentIndex,
                    totalCount = totalCount,
                    trainType = currentTrainType.label,
                    remainingTime = remainingTime,
                )
                HorizontalPager(
                    modifier = Modifier.weight(1f),
                    state = pagerState,
                    userScrollEnabled = false,
                ) { i ->
                    WordTrainPage(
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
            text = "Train Type: $trainType", // TODO, string res
            style = MaterialTheme.typography.bodyLarge
        )
        MDLinearProgressIndicator(
            progress = currentIndex,
            total = totalCount,
            markLastProgressedItemAsDone = false
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${currentIndex.inc()}/$totalCount", // TODO, string res
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

    remainingTime.remainingTime
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
    onSelectAnswerSubmit: (Int?) -> Unit,
    onWriteWordSubmit: (String?) -> Unit,
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
    onSubmit: (Int?) -> Unit,
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
    ) {
        QuestionPagePart(
            modifier = Modifier.weight(1f),
            question = train.question,
            currentAnswer = selectedAnswer,
            onSubmit = {
                onSubmit(selectedAnswerIndex.takeUnless { it < 0 })
            },
            onSubmitEnabled = onSubmitEnabled
        )
        Column(
            modifier = Modifier,
        ) {
            train.options.forEachIndexed { i, option ->
                val selected by remember(i, selectedAnswerIndex) {
                    derivedStateOf { i == selectedAnswerIndex }
                }
                MDListItem(
                    onClick = {
                        selectedAnswerIndex = i
                    },
                    leadingContent = {
                        if (selected) {
                            MDIcon(MDIconsSet.Check) // checked
                        } else {
                            Box(modifier = Modifier.width(24.dp))
                        }
                    },
                    trailingContent = {
                        // to keep the title in the middle
                        Box(modifier = Modifier.width(24.dp))
                    },
                    headlineContent = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(option)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun WordWriteTrainPage(
    train: MDTrainWordQuestion.WriteWord,
    onSubmit: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var answer by remember {
        mutableStateOf("")
    }
    Column(
        modifier = modifier,
    ) {
        QuestionPagePart(
            modifier = Modifier.weight(1f),
            question = train.question,
            currentAnswer = answer,
            onSubmit = {
                onSubmit(answer)
            },
        )
        Column(modifier = Modifier.weight(1f)) {
            MDBasicTextField(
                value = answer,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    answer = it
                },
                placeholder = "Write answer here"
            )
        }
    }
}

@Composable
private fun QuestionPagePart(
    question: String,
    currentAnswer: String,
    onSubmit: () -> Unit,
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
        AssistChip(
            onClick = onSubmit,
            enabled = onSubmitEnabled,
            label = {
                Text("Submit answer") // TODO, string res
            },
            trailingIcon = {
                MDIcon(
                    icon = MDIconsSet.ArrowForward,
                    contentDescription = null
                ) // checked
            }
        )
    }
}
