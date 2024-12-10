package dev.bayan_ibrahim.my_dictionary.domain.model.train_history

import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

data class TrainHistory(
    val time: Long = System.currentTimeMillis(),
    val trainType: TrainWordType,
    val words: List<WordTrainHistory>,
)
