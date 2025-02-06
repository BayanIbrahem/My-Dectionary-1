package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID

data class WordClassRelation(
    val label: String,
    val id: Long = INVALID_ID,
    val wordsCount: Int = 0
) {
    infix fun similar(other: WordClassRelation): Boolean {
        return label == other.label
    }
}
