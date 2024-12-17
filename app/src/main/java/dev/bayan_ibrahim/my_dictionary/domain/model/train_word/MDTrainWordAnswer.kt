package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlin.time.Duration

sealed interface MDTrainWordAnswer {
    val word: Word
    val selectedAnswer: String?
    val correctAnswer: String
    val consumedDuration: Duration
    val isTimeOut: Boolean
    val type: TrainWordType

    data class SelectAnswer(
        override val word: Word,
        override val selectedAnswer: String?,
        override val correctAnswer: String,
        override val consumedDuration: Duration,
        override val isTimeOut: Boolean,
    ) : MDTrainWordAnswer {
        override val type: TrainWordType = TrainWordType.SelectWordMeaning
    }

    data class WriteWord(
        override val word: Word,
        override val selectedAnswer: String?,
        override val correctAnswer: String,
        override val consumedDuration: Duration,
        override val isTimeOut: Boolean,
    ) : MDTrainWordAnswer {
        override val type: TrainWordType = TrainWordType.WriteWord
    }
}

fun MDTrainWordAnswer.asResult(): MDTrainWordResult = if (isTimeOut) {
    MDTrainWordResult.Timeout(consumedDuration = consumedDuration)
} else if (selectedAnswer == null) {
    MDTrainWordResult.Pass(consumedDuration = consumedDuration)
} else if (selectedAnswer == correctAnswer) {
    MDTrainWordResult.Right(
        consumedDuration = consumedDuration,
        correctAnswer = correctAnswer
    )
} else {
    MDTrainWordResult.Wrong(
        consumedDuration = consumedDuration,
        selectedAnswer = selectedAnswer ?: "-",
        correctAnswer = correctAnswer,
    )
}