package dev.bayan_ibrahim.my_dictionary.domain.model.train_history

import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TrainHistory(
    val time: Instant = Clock.System.now(),
    val trainType: TrainWordType,
    val words: List<WordTrainHistory>,
)
