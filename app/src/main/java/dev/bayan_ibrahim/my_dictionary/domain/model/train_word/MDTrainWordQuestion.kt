package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlin.time.Duration

@Immutable
sealed interface MDTrainWordQuestion {
    val word: Word
    val type: TrainWordType
    val questionSelector: (Word) -> String

    val question: String
        get() = questionSelector(word)

    @Immutable
    data class SelectAnswer(
        override val word: Word,
        override val questionSelector: (Word) -> String,
        val options: List<String>,
        val correctOptionIndex: Int,
    ) : MDTrainWordQuestion {
        override val type = TrainWordType.SelectWordMeaning

    }

    @Immutable
    data class WriteWord(
        override val word: Word,
        override val questionSelector: (Word) -> String,
        val answerSelector: (Word) -> String,
    ) : MDTrainWordQuestion {
        override val type = TrainWordType.WriteWord

        val answer: String
            get() = answerSelector(word)
    }
}


fun MDTrainWordQuestion.SelectAnswer.toAnswer(
    selectedIndex: Int,
    submitOption: MDTrainSubmitOption,
    consumedDuration: Duration,
): MDTrainWordAnswer.SelectAnswer = MDTrainWordAnswer.SelectAnswer(
    word = word,
    selectedAnswer = options.getOrNull(selectedIndex),
    correctAnswer = options[correctOptionIndex],
    submitOption = submitOption,
    consumedDuration = consumedDuration,
    isTimeOut = false
)

fun MDTrainWordQuestion.SelectAnswer.toTimeoutAnswer(
    maxDuration: Duration,
): MDTrainWordAnswer.SelectAnswer = MDTrainWordAnswer.SelectAnswer(
    word = word,
    selectedAnswer = null,
    correctAnswer = options[correctOptionIndex],
    submitOption = MDTrainSubmitOption.Answer,
    consumedDuration = maxDuration,
    isTimeOut = true
)

fun MDTrainWordQuestion.WriteWord.toAnswer(
    answer: String,
    submitOption: MDTrainSubmitOption,
    consumedDuration: Duration,
): MDTrainWordAnswer.WriteWord = MDTrainWordAnswer.WriteWord(
    word = word,
    selectedAnswer = answer.nullIfInvalid(),
    correctAnswer = this.answer,
    submitOption = submitOption,
    consumedDuration = consumedDuration,
    isTimeOut = false
)

fun MDTrainWordQuestion.WriteWord.toTimeoutAnswer(
    maxDuration: Duration,
): MDTrainWordAnswer.WriteWord = MDTrainWordAnswer.WriteWord(
    word = word,
    selectedAnswer = null,
    correctAnswer = this.answer,
    submitOption = MDTrainSubmitOption.Answer,
    consumedDuration = maxDuration,
    isTimeOut = true
)
