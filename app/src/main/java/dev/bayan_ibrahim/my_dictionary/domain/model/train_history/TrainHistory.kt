package dev.bayan_ibrahim.my_dictionary.domain.model.train_history

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TrainHistory(
    val time: Instant = Clock.System.now(),
    val words: List<WordTrainHistory>,
)
