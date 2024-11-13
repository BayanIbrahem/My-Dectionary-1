package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID

data class WordTypeTagRelation(
    val label: String,
    val id: Long = INVALID_ID,
    val wordsCount: Int = 0
) {
    infix fun similar(other: WordTypeTagRelation): Boolean {
        return label == other.label
    }
}
