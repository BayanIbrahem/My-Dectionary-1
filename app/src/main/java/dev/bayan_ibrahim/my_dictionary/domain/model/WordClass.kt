package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

data class WordClass(
    val id: Long,
    val name: String,
    val language: Language,
    val relations: List<WordClassRelation>,
    val wordsCount: Int = 0
) {
    infix fun similar(other: WordClass): Boolean {
        return name == other.name && language.code == other.language.code && (relations.map { it.label }.toSet() == other.relations.map { it.label }.toSet())
    }
}

