package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID

data class WordWordClassRelation(
    val label: String,
    val id: Long = INVALID_ID,
    val wordsCount: Int = 0
) {
    infix fun similar(other: WordWordClassRelation): Boolean {
        return label == other.label
    }
}
