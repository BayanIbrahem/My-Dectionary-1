package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.and
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.levensteinDistance
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.maxN.maxNBy
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.minN.minNBy
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.minN.subList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.safeSubList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.WordTrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWord
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordAnswer
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.answerSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.questionSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class MDTrainViewModel @Inject constructor(
    private val repo: MDTrainRepo,
) : ViewModel() {
    private val _uiState: MDTrainMutableUiState = MDTrainMutableUiState()
    val uiState: MDTrainUiState = _uiState

    private val answersList: MutableList<TrainWordAnswer> = mutableListOf()

    fun initWithNavArgs(args: MDDestination.Train) {
        viewModelScope.launch {
            _uiState.onExecute {
                val trainPreferences: MDWordsListTrainPreferences = repo.getTrainPreferences()
                val viewPreferences = repo.getViewPreferences()
                val idsOfAllowedTagsAndProgressRange: Set<Long> = repo.getWordsIdsOfTagsAndProgressRange(viewPreferences)
                val allWords: Sequence<Word> = repo.getAllSelectedLanguageWords()

                val validWords = allWords.filter { word ->
                    word.id in idsOfAllowedTagsAndProgressRange && viewPreferences.matches(word)
                }
                val wordsList = sortWordsByPreferences(
                    sortBy = trainPreferences.sortBy,
                    sortByOrder = trainPreferences.sortByOrder,
                    validWords = validWords,
                    getTrainHistory = {
                        getWordsLastTrainHistories(idsOfAllowedTagsAndProgressRange)
                    },
                    limit = trainPreferences.limit.count
                )

                val trainWords = generateTrainWords(
                    allWords = validWords,
                    targetWords = wordsList,
                    trainType = trainPreferences.trainType,
                    trainTarget = trainPreferences.trainTarget
                )

                answersList.clear()
                _uiState.trainWordsList.setAll(trainWords)
                _uiState.trainType = trainPreferences.trainType

                wordsList.isNotEmpty()
            }
        }
    }

    private inline fun sortWordsByPreferences(
        sortBy: WordsListTrainPreferencesSortBy,
        sortByOrder: WordsListSortByOrder,
        validWords: Sequence<Word>,
        getTrainHistory: () -> Map<Long, Instant>,
        limit: Int,
    ): List<Word> {
        if (sortBy == WordsListTrainPreferencesSortBy.Random) {
            return validWords.shuffled().iterator().subList(limit)
        }
        val trainHistory = if (sortBy == WordsListTrainPreferencesSortBy.TrainingTime) {
            getTrainHistory()
        } else {
            emptyMap()
        }

        @Suppress("KotlinConstantConditions")
        val selector = when (sortBy) {
            WordsListTrainPreferencesSortBy.LearningProgress -> {
                { word: Word -> word.learningProgress.toDouble() }
            }

            WordsListTrainPreferencesSortBy.TrainingTime -> {
                { it: Word -> trainHistory[it.id]?.epochSeconds?.toDouble() ?: 0.0 }
            }

            WordsListTrainPreferencesSortBy.CreateTime -> {
                { it: Word -> it.createdAt.epochSeconds.toDouble() }
            }

            WordsListTrainPreferencesSortBy.Random -> {
                { it: Word -> it.id.toDouble() }
            }
        }
        return when (sortByOrder) {
            WordsListSortByOrder.Asc -> validWords.minNBy(maxCount = limit, selector)
            WordsListSortByOrder.Desc -> validWords.maxNBy(maxCount = limit, selector)
        }
    }

    private fun generateTrainWords(
        allWords: Sequence<Word>,
        targetWords: List<Word>,
        trainType: TrainWordType,
        trainTarget: WordsListTrainTarget,
    ): List<TrainWord> {
        val questionSelector = trainTarget.questionSelector
        val answerSelector = trainTarget.answerSelector
        return when (trainType) {
            TrainWordType.SelectWordMeaning -> {
                targetWords.map { targetWord ->
                    val targetAnswer = answerSelector(targetWord)
                    val similarTags = allWords.maxNBy(maxCount = TrainWordType.SELECTION_TAGS_GROUP_COUNT) { otherWord ->
                        // we don't need the same word
                        if (otherWord.id == targetWord.id) return@maxNBy Int.MIN_VALUE

                        (targetWord.tags and otherWord.tags).count()
                    }
                    val similarWords = allWords.minNBy(maxCount = TrainWordType.SELECTION_LEVENSHTEIN_GROUP_COUNT) { otherWord ->
                        // we don't need the same word
                        if (otherWord.id == targetWord.id) return@minNBy Float.MAX_VALUE

                        levensteinDistance(
                            s1 = targetAnswer,
                            s2 = answerSelector(otherWord)
                        )
                    }
                    val optionsWords =
                        ((similarTags + similarWords).shuffled().distinct()
                            .safeSubList(0, TrainWordType.MAX_SELECTIONS_COUNT.dec()) + targetWord).shuffled()
                    val currentCorrectOption = optionsWords.indexOfFirst { it.id == targetWord.id }
                    TrainWord.SelectAnswer(
                        word = targetWord,
                        questionSelector = questionSelector,
                        options = optionsWords.map(answerSelector),
                        correctOptionIndex = currentCorrectOption,
                    )
                }
            }

            TrainWordType.WriteWord -> {
                return targetWords.map { word ->
                    TrainWord.WriteWord(
                        word = word,
                        questionSelector = questionSelector,
                        answerSelector = answerSelector
                    )
                }
            }
        }
    }

    /**
     * @param lastTrainHistory used when [sortBy] is [WordsListTrainPreferencesSortBy.TrainingTime]
     * @return a shuffled sublist with size [limit] from first [limit] unique value according to [sortBy]
     */
    private fun limitedSubWords(
        limit: Int,
        words: Sequence<Word>,
        lastTrainHistory: Map<Long, Long>,
        sortBy: WordsListTrainPreferencesSortBy,
    ): List<Word> {
        val lastIndex: Int? = when (sortBy) {
            WordsListTrainPreferencesSortBy.LearningProgress -> {
                var lastValue: Float? = null
                var distinctValuesCount = 0
                words.indexOfFirst {
                    if (it.learningProgress != lastValue) {
                        distinctValuesCount++
                        lastValue = it.learningProgress
                    }
                    distinctValuesCount == limit
                }
            }

            WordsListTrainPreferencesSortBy.TrainingTime -> {
                var lastValue: Long? = null
                var distinctValuesCount = 0
                words.indexOfFirst {
                    val trainHistory = lastTrainHistory[it.id] ?: 0
                    if (trainHistory != lastValue) {
                        distinctValuesCount++
                        lastValue = trainHistory
                    }
                    distinctValuesCount == limit
                }
            }

            WordsListTrainPreferencesSortBy.CreateTime -> {
                var lastValue: Instant? = null
                var distinctValuesCount = 0
                words.indexOfFirst {
                    if (it.createdAt != lastValue) {
                        distinctValuesCount++
                        lastValue = it.createdAt
                    }
                    distinctValuesCount == limit
                }
            }

            WordsListTrainPreferencesSortBy.Random -> {
                return words.shuffled().toList().subList(0, minOf(words.count(), limit.inc()))
            }
        }.takeUnless { it < 0 }
        return if (lastIndex == null) {
            words.toList().shuffled()
        } else {
            words.toList().subList(0, lastIndex.inc()).shuffled().subList(0, limit)
        }
    }

    private suspend fun getWordsLastTrainHistories(wordsIds: Set<Long>): MutableMap<Long, Instant> {
        val lastTrainHistories: MutableMap<Long, Instant> = mutableMapOf()
        repo.getTrainHistoryOf(
            wordsIds = wordsIds,
            excludeSpecifiedWordsIds = false
        ).firstOrNull().let { it ?: emptyList() }.forEach { history ->
            history.words.forEach { wordHistory ->
                val currentTrainHistory = lastTrainHistories[wordHistory.wordId]
                if (currentTrainHistory == null || currentTrainHistory < history.time) {
                    lastTrainHistories[wordHistory.wordId] = history.time
                }
            }
        }
        return lastTrainHistories
    }

    fun getUiActions(
        navActions: MDTrainNavigationUiActions,
    ): MDTrainUiActions = MDTrainUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun onEndTrain(onSubmit: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val trainHistory = TrainHistory(
                trainType = uiState.trainType,
                words = answersList.map { answer ->
                    WordTrainHistory(
                        id = null,
                        wordId = answer.word.id,
                        meaningSnapshot = answer.correctAnswer,
                        trainResult = if (answer.isTimeout) {
                            TrainWordResult.Timeout
                        } else if (answer.isPass) {
                            TrainWordResult.Pass
                        } else {
                            TrainWordResult.Fail(
                                selectedAnswer = answer.selectedAnswer ?: "",
                                currentAnswer = answer.correctAnswer
                            )
                        }
                    )
                }
            )
            repo.submitTrainHistory(trainHistory)
            onSubmit()
        }
    }

    private fun getBusinessUiActions(
        navActions: MDTrainNavigationUiActions,
    ): MDTrainBusinessUiActions = object : MDTrainBusinessUiActions {
        override fun onSelectAnswer(answer: TrainWordAnswer) {
            answersList.add(answer)
            if (uiState.isLast) {
                onEndTrain {
                    viewModelScope.launch(Dispatchers.Main.immediate) {
                        navActions.onNavigateToResultsScreen()
                    }
                }
            } else {
                _uiState.currentWordIndex++
            }
        }
    }
}

