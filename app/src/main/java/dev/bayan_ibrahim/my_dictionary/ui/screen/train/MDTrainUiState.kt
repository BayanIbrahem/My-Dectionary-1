package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordQuestion
import kotlin.time.Duration

sealed interface MDTrainUiState : MDUiState {
    data object Loading : MDTrainUiState {
        override val isLoading: Boolean = true
        override val error: String? = null
        override val validData: Boolean = true
    }

    data class AnswerWord(
        val trainWordsListQuestion: List<MDTrainWordQuestion>,
        val currentIndex: Int,
    ) : MDTrainUiState {
        override val isLoading: Boolean = false
        override val error: String? = null
        override val validData: Boolean = trainWordsListQuestion.isNotEmpty() && currentIndex < trainWordsListQuestion.count()
    }

    data object Finish : MDTrainUiState {
        override val isLoading: Boolean = false
        override val error: String? = null
        override val validData: Boolean = true
    }
}

data class MDTrainWordAnswerTime(
    val remainingTime: Duration,
    val totalTime: Duration,
) {
    val percent = remainingTime / totalTime

    constructor(totalTime: Duration) : this(totalTime, totalTime)

    operator fun plus(
        duration: Duration,
    ): MDTrainWordAnswerTime = copy(
        remainingTime = remainingTime.plus(duration)
    )

    operator fun minus(
        duration: Duration,
    ): MDTrainWordAnswerTime = copy(
        remainingTime = remainingTime.minus(duration)
    )
}