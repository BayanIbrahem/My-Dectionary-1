package dev.bayan_ibrahim.my_dictionary.data_source.local.train

import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word

interface MDTrainMemoryDecayFactorDataSource {
    fun calculateDecayOf(word: Word, answer: MDTrainWordResult): Float
}