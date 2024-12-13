package dev.bayan_ibrahim.my_dictionary.domain.model.train_history

import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResult

data class WordTrainHistory(
    val id: Long?,
    val wordId: Long?,
    val meaningSnapshot: String,
    val trainResult: TrainWordResult
)

