package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.progress_indicator.linear.MDLinearProgressIndicator
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWord
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordAnswer
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.toAnswer
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.train.component.MDTrainTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDTrainScreen(
    uiState: MDTrainUiState,
    uiActions: MDTrainUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        contentWindowInsets = WindowInsets(8.dp, 8.dp, 8.dp, 8.dp),
        topBar = {
            MDTrainTopAppBar()
        },
    ) {
        val pagerState = rememberPagerState { uiState.trainWordsList.size }
        LaunchedEffect(uiState.currentWordIndex) {
            pagerState.animateScrollToPage(uiState.currentWordIndex)
        }
        Column {
            ScreenHeader(
                currentIndex = uiState.currentWordIndex,
                totalCount = uiState.trainWordsList.count(),
                trainType = uiState.trainType.label
            )
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
                userScrollEnabled = false,
            ) { i ->
                WordTrainPage(uiState.trainWordsList[i], onSubmit = uiActions::onSelectAnswer)
            }
        }
    }
}

@Composable
private fun ScreenHeader(
    currentIndex: Int,
    totalCount: Int,
    trainType: String,
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
        Text(
            text = "${currentIndex.inc()}/$totalCount", // TODO, string res
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun WordTrainPage(
    train: TrainWord,
    onSubmit: (TrainWordAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (train) {
        is TrainWord.SelectAnswer -> WordSelectAnswerTrainPage(
            train = train,
            onSubmit = onSubmit,
            modifier = modifier
        )

        is TrainWord.WriteWord -> WordWriteTrainPage(
            train = train,
            onSubmit = onSubmit,
            modifier = modifier
        )
    }
}

@Composable
private fun WordSelectAnswerTrainPage(
    train: TrainWord.SelectAnswer,
    onSubmit: (TrainWordAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedAnswerIndex: Int by rememberSaveable {
        mutableIntStateOf(-1)
    }
    val selectedAnswer: String by remember(selectedAnswerIndex) {
        derivedStateOf {
            if (selectedAnswerIndex < 0) {
                ""
            } else {
                train.options[selectedAnswerIndex]
            }
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
                val answer = train.toAnswer(selectedAnswerIndex)
                onSubmit(answer)
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
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
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
    train: TrainWord.WriteWord,
    onSubmit: (TrainWordAnswer) -> Unit,
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
                onSubmit(train.toAnswer(answer))
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
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        )
    }
}

@Preview
@Composable
private fun TrainScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val word = Word(
                    id = 0,
                    meaning = "Auge",
                    translation = "Eye",
                    additionalTranslations = listOf("Human Eye", "Human Eye 2"),
                    language = Language("de".code, "Deutsch", "German"),
                    tags = setOf("Human body", "Organic"),
                    transcription = "auge",
                    examples = listOf("I habe zwei auge", "some other example"),
                    createdAt = 0,
                    updatedAt = 0,
                    wordTypeTag = WordTypeTag(
                        id = 0,
                        name = "name",
                        language = Language("de".code, "Deutsch", "German"),
                        relations = listOf(WordTypeTagRelation("relation 1"), WordTypeTagRelation("relation 2")),
                        wordsCount = 30,
                    )
                )

                val uiState by remember {
                    derivedStateOf {
                        MDTrainMutableUiState().apply {
                            onExecute {
                                this.trainWordsList.addAll(
                                    listOf(
                                        TrainWord.SelectAnswer(
                                            word = word,
                                            questionSelector = { word.meaning },
                                            options = listOf(
                                                "Option 1",
                                                "Option 2",
                                                "Option 3",
                                            ),
                                            correctOptionIndex = 0,
                                        ),
                                        TrainWord.WriteWord(
                                            word = word,
                                            questionSelector = { word.meaning },
                                            answerSelector = { word.translation },
                                        )
                                    )
                                )
                                true
                            }
                        }
                    }
                }
                MDTrainScreen(
                    uiState = uiState,
                    uiActions = MDTrainUiActions(
                        object : MDTrainNavigationUiActions {
                            override fun onNavigateToResultsScreen() {
                            }
                        },
                        object : MDTrainBusinessUiActions {
                            override fun onSelectAnswer(answer: TrainWordAnswer) {
                                uiState.currentWordIndex = uiState.currentWordIndex
                                    .inc()
                                    .mod(uiState.trainWordsList.size)
                            }
                        },
                    )
                )
            }
        }
    }
}
