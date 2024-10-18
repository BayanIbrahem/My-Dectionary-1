package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.domain.model.Language

data class WordTypeTag(
    val id: Long,
    val name: String,
    val language: Language,
    val relations: List<String>,
)
