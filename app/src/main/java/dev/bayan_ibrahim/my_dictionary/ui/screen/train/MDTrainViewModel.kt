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
import dev.bayan_ibrahim.my_dictionary.data_source.local.timer.MDTimerDataSource
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainMemoryDecayFactorAnswerDurationBasedDataSource
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainMemoryDecayFactorDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.WordTrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainSubmitOption
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordAnswer
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordQuestion
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.asResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.toAnswer
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.toTimeoutAnswer
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainHistoryRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.ViewPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.answerSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.questionSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MDTrainViewModel @Inject constructor(
    private val wordRepo: WordRepo,
    private val userPreferencesRepo: UserPreferencesRepo,
    private val viewPreferencesRepo: ViewPreferencesRepo,
    private val trainPreferencesRepo: TrainPreferencesRepo,
    private val trainHistoryRepo: TrainHistoryRepo,
    private val timer: MDTimerDataSource,
) : ViewModel() {
    private val trainDataSource = MDTrainDataSource.Default
    private val defaultAnswerTime = 30.seconds
    private val maxAvailableAnswerTime = 60.seconds
    private val timeSourceStep = 100.milliseconds
    private val decayFactorDataSource: MDTrainMemoryDecayFactorDataSource = MDTrainMemoryDecayFactorAnswerDurationBasedDataSource(
        defaultAnswerDuration = defaultAnswerTime,
        maxAnswerDuration = maxAvailableAnswerTime
    )

    private val _uiState: MutableStateFlow<MDTrainUiState> = MutableStateFlow(MDTrainUiState.Loading)
    val uiState: StateFlow<MDTrainUiState> = _uiState.asStateFlow()
    private var _wordRemainingTimeDataSource = MutableStateFlow(MDTrainWordAnswerTime(maxAvailableAnswerTime))
    val wordRemainingTimeDataSource = _wordRemainingTimeDataSource.asStateFlow()

    private val answers: MutableMap<MDTrainWordQuestion, MDTrainWordAnswer> = mutableMapOf()

    private var wordRemainingTimeDataSourceJob: Job? = null
    private fun initWordRemainingTimeDataSource() {
        wordRemainingTimeDataSourceJob?.cancel()
        wordRemainingTimeDataSourceJob = viewModelScope.launch {
            timer.assignFixedRateTimer(
                maxAvailableAnswerTime,
                timeSourceStep,
            ).collect {
                if (it.completed) { // timeout
                    onTimeOut()
                }
                val time = MDTrainWordAnswerTime(it.duration - it.passedDuration, it.duration)
                _wordRemainingTimeDataSource.emit(time)
            }
        }
    }

    private var disposed: Boolean = true

    fun initWithNavArgs(args: MDDestination.Train) {
        if (!disposed) return
        disposed = false

        viewModelScope.launch {
            _uiState.emit(MDTrainUiState.Loading)
            val trainPreferences: MDWordsListTrainPreferences = trainPreferencesRepo.getTrainPreferences()
            val viewPreferences = viewPreferencesRepo.getViewPreferences()
            val selectedLanguage = userPreferencesRepo.getUserPreferences().selectedLanguagePage ?: let {
                // TODO, handle invalid language
                _uiState.emit(MDTrainUiState.Finish)
                return@launch
            }
            val idsOfAllowedTagsAndProgressRange: Set<Long> = wordRepo.getWordsIdsOf(
                languages = setOf(selectedLanguage),
                contextTags = viewPreferences.selectedTags.map { it.id }.toSet(),
                memorizingProbabilities = viewPreferences.selectedMemorizingProbabilityGroups
            ).first()

            val allWords: Sequence<Word> = wordRepo.getWordsOf(setOf(selectedLanguage)).first()

            val validWords = allWords.filter { word ->
                word.id in idsOfAllowedTagsAndProgressRange && viewPreferences.matches(word)
            }
            val wordsList = sortWordsByPreferences(
                sortBy = trainPreferences.sortBy,
                sortByOrder = trainPreferences.sortByOrder,
                validWords = validWords,
                limit = trainPreferences.limit.count
            )

            val trainWords = generateTrainWords(
                allWords = validWords,
                targetWords = wordsList,
                trainType = trainPreferences.trainType,
                trainTarget = trainPreferences.trainTarget
            )

            answers.clear()
            _uiState.value = MDTrainUiState.AnswerWord(trainWords, 0)

            initWordRemainingTimeDataSource()
        }
    }

    private fun sortWordsByPreferences(
        sortBy: WordsListTrainPreferencesSortBy,
        sortByOrder: MDWordsListSortByOrder,
        validWords: Sequence<Word>,
        limit: Int,
    ): List<Word> {
        if (sortBy == WordsListTrainPreferencesSortBy.Random) {
            return validWords.shuffled().iterator().subList(limit)
        }
        @Suppress("KotlinConstantConditions")
        val selector = when (sortBy) {
            WordsListTrainPreferencesSortBy.MemorizingProbability -> {
                { it: Word -> trainDataSource.memoryDecayFormula(it).toDouble() }
//                { word: Word -> word.memoryDecayFactor.toDouble() }
            }

            WordsListTrainPreferencesSortBy.TrainingTime -> {
                { it: Word -> it.lastTrainTime?.epochSeconds?.toDouble() ?: 0.0 }
            }

            WordsListTrainPreferencesSortBy.CreateTime -> {
                { it: Word -> it.createdAt.epochSeconds.toDouble() }
            }

            WordsListTrainPreferencesSortBy.Random -> {
                { it: Word -> it.id.toDouble() }
            }
        }
        return when (sortByOrder) {
            MDWordsListSortByOrder.Asc -> validWords.minNBy(maxCount = limit, selector)
            MDWordsListSortByOrder.Desc -> validWords.maxNBy(maxCount = limit, selector)
        }
    }

    private fun generateTrainWords(
        allWords: Sequence<Word>,
        targetWords: List<Word>,
        trainType: TrainWordType,
        trainTarget: WordsListTrainTarget,
    ): List<MDTrainWordQuestion> {
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
                    MDTrainWordQuestion.SelectAnswer(
                        word = targetWord,
                        questionSelector = questionSelector,
                        options = optionsWords.map(answerSelector),
                        correctOptionIndex = currentCorrectOption,
                    )
                }
            }

            TrainWordType.WriteWord -> {
                return targetWords.map { word ->
                    MDTrainWordQuestion.WriteWord(
                        word = word,
                        questionSelector = questionSelector,
                        answerSelector = answerSelector
                    )
                }
            }
        }
    }

    fun getUiActions(
        navActions: MDTrainNavigationUiActions,
    ): MDTrainUiActions = MDTrainUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun MDTrainUiState.AnswerWord.onEndTrain() {
        _uiState.value = MDTrainUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val trainHistory = generateTrainHistoryOfAnswers()
            val wordsMemoryDecay = answers.map { (ques, ans) ->
                val newDecayFactor = decayFactorDataSource.calculateDecayOf(ques.word, ans.asResult())
                ques.word.id to newDecayFactor
            }.toMap()
            trainHistoryRepo.submitTrainHistory(trainHistory, wordsMemoryDecay)
            _uiState.value = MDTrainUiState.Finish
        }
    }

    private fun generateTrainHistoryOfAnswers() = TrainHistory(
        words = answers.map { (train, answer) ->
            WordTrainHistory(
                id = null,
                wordId = answer.word.id,
                questionWord = train.question,
                trainResult = answer.asResult(),
                trainType = train.type
            )
        }
    )

    private fun onTimeOut() {
        val state = uiState.value
        if (state is MDTrainUiState.AnswerWord) {
            val index = state.currentIndex
            val question = state.trainWordsListQuestion[index]
            val answer = when (question) {
                is MDTrainWordQuestion.SelectAnswer -> question.toTimeoutAnswer(maxAvailableAnswerTime)

                is MDTrainWordQuestion.WriteWord -> question.toTimeoutAnswer(maxAvailableAnswerTime)
            }
            state.onAnswerQuestion(question, answer)
        }
    }

    private fun MDTrainUiState.AnswerWord.onAnswerQuestion(train: MDTrainWordQuestion, answer: MDTrainWordAnswer) {
        answers[train] = answer
        val index = this.currentIndex
        val isLast = index == this.trainWordsListQuestion.count().dec()
        if (isLast) {
            onEndTrain()
        } else {
            _uiState.value = this.copy(currentIndex = index.inc())
            initWordRemainingTimeDataSource()
        }
    }

    private fun getBusinessUiActions(
        navActions: MDTrainNavigationUiActions,
    ): MDTrainBusinessUiActions = object : MDTrainBusinessUiActions {
        override fun onSelectAnswer(answer: MDTrainWordAnswer) {
            val state = uiState.value
            if (state is MDTrainUiState.AnswerWord) {
                val index = state.currentIndex
                val question = state.trainWordsListQuestion[index]
                state.onAnswerQuestion(question, answer)
            }
        }

        override fun onSelectAnswerSubmit(index: Int, submitOption: MDTrainSubmitOption) {
            val state = uiState.value
            val time = wordRemainingTimeDataSource.value
            if (state is MDTrainUiState.AnswerWord) {
                val currentIndex = state.currentIndex
                val question = state.trainWordsListQuestion[currentIndex]
                if (question is MDTrainWordQuestion.SelectAnswer) {
                    state.onAnswerQuestion(
                        train = question,
                        answer = question.toAnswer(
                            selectedIndex = index,
                            consumedDuration = time.totalTime - time.remainingTime,
                            submitOption = submitOption,
                        )
                    )
                }
            }
        }

        override fun onWriteWordSubmit(answer: String, submitOption: MDTrainSubmitOption) {
            val state = uiState.value
            val time = wordRemainingTimeDataSource.value
            if (state is MDTrainUiState.AnswerWord) {
                val currentIndex = state.currentIndex
                val question = state.trainWordsListQuestion[currentIndex]
                if (question is MDTrainWordQuestion.WriteWord) {
                    state.onAnswerQuestion(
                        train = question,
                        answer = question.toAnswer(
                            answer = answer,
                            consumedDuration = time.remainingTime,
                            submitOption = submitOption,
                        )
                    )
                }
            }
        }
    }

    fun onDispose() {
        disposed = true
    }
}

