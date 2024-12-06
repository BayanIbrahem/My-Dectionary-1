package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

data class WordTypeTag(
    val id: Long,
    val name: String,
    val language: Language,
    val relations: List<WordTypeTagRelation>,
    val wordsCount: Int = 0
) {
    infix fun similar(other: WordTypeTag): Boolean {
        return name == other.name && language.code == other.language.code && (relations.map { it.label }.toSet() == other.relations.map { it.label }.toSet())
    }
}

