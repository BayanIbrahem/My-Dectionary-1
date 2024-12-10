package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.compose.runtime.Immutable
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word

@Immutable
sealed interface TrainWord {
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
    ) : TrainWord {
        override val type = TrainWordType.SelectWordMeaning

    }

    @Immutable
    data class WriteWord(
        override val word: Word,
        override val questionSelector: (Word) -> String,
        val answerSelector: (Word) -> String,
    ) : TrainWord {
        override val type = TrainWordType.WriteWord

        val answer: String
            get() = answerSelector(word)
    }
}


fun TrainWord.SelectAnswer.toAnswer(selectedIndex: Int): TrainWordAnswer.SelectAnswer {
    return TrainWordAnswer.SelectAnswer(
        word = word,
        selectedAnswer = options[selectedIndex],
        correctAnswer = options[correctOptionIndex]
    )
}
fun TrainWord.WriteWord.toAnswer(answer: String): TrainWordAnswer.WriteWord {
    return TrainWordAnswer.WriteWord(
        word = word,
        selectedAnswer = answer,
        correctAnswer = this.answer,
    )
}
