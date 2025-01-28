package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlin.time.Duration

sealed interface MDTrainWordAnswer {
    val word: Word
    val selectedAnswer: String?
    val correctAnswer: String
    val submitOption: MDTrainSubmitOption
    val consumedDuration: Duration
    val isTimeOut: Boolean
    val type: TrainWordType

    data class SelectAnswer(
        override val word: Word,
        override val selectedAnswer: String?,
        override val correctAnswer: String,
        override val submitOption: MDTrainSubmitOption,
        override val consumedDuration: Duration,
        override val isTimeOut: Boolean,
    ) : MDTrainWordAnswer {
        override val type: TrainWordType = TrainWordType.SelectWordMeaning
    }

    data class WriteWord(
        override val word: Word,
        override val selectedAnswer: String?,
        override val correctAnswer: String,
        override val submitOption: MDTrainSubmitOption,
        override val consumedDuration: Duration,
        override val isTimeOut: Boolean,
    ) : MDTrainWordAnswer {
        override val type: TrainWordType = TrainWordType.WriteWord
    }
}

fun MDTrainWordAnswer.asResult(): MDTrainWordResult = if (isTimeOut) {
    MDTrainWordResult.Timeout(consumedDuration = consumedDuration)
} else if (selectedAnswer == null || submitOption == MDTrainSubmitOption.Pass) {
    MDTrainWordResult.Pass(consumedDuration = consumedDuration)
} else if (selectedAnswer?.meaningViewNormalize == correctAnswer.meaningViewNormalize) {
    MDTrainWordResult.Right(
        consumedDuration = consumedDuration,
        correctAnswer = correctAnswer,
        submitOption = submitOption
    )
} else {
    MDTrainWordResult.Wrong(
        consumedDuration = consumedDuration,
        selectedAnswer = selectedAnswer ?: "-",
        correctAnswer = correctAnswer,
        submitOption = submitOption
    )
}