package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word

sealed interface TrainWordAnswer {
    val word: Word
    val selectedAnswer: String?
    val correctAnswer: String
    val isPass: Boolean get() = selectedAnswer == correctAnswer
    val isTimeout: Boolean get() = selectedAnswer == null

    val type: TrainWordType

    data class SelectAnswer(
        override val word: Word,
        override val selectedAnswer: String?,
        override val correctAnswer: String,
    ) : TrainWordAnswer {
        override val type: TrainWordType = TrainWordType.SelectWordMeaning
    }

    data class WriteWord(
        override val word: Word,
        override val selectedAnswer: String?,
        override val correctAnswer: String,
    ) : TrainWordAnswer {
        override val type: TrainWordType = TrainWordType.WriteWord
    }
}