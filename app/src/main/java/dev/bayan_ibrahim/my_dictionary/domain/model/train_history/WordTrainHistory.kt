package dev.bayan_ibrahim.my_dictionary.domain.model.train_history

import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

data class WordTrainHistory(
    val id: Long?,
    val wordId: Long?,
    val questionWord: String,
    val trainResult: MDTrainWordResult,
    val trainType: TrainWordType,
)

