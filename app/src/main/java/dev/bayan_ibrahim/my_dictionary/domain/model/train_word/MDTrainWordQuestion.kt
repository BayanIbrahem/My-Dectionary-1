package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.compose.runtime.Immutable
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
    selectedIndex: Int?,
    consumedDuration: Duration,
    maximumAllowedDuration: Duration,
): MDTrainWordAnswer.SelectAnswer = MDTrainWordAnswer.SelectAnswer(
    word = word,
    selectedAnswer = selectedIndex?.let { options[it] },
    correctAnswer = options[correctOptionIndex],
    consumedDuration = consumedDuration,
    isTimeOut = consumedDuration >= maximumAllowedDuration
)

fun MDTrainWordQuestion.WriteWord.toAnswer(
    answer: String?,
    consumedDuration: Duration,
    maximumAllowedDuration: Duration,
): MDTrainWordAnswer.WriteWord = MDTrainWordAnswer.WriteWord(
    word = word,
    selectedAnswer = answer,
    correctAnswer = this.answer,
    consumedDuration = consumedDuration,
    isTimeOut = consumedDuration >= maximumAllowedDuration
)
