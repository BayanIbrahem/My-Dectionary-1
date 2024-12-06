package dev.bayan_ibrahim.my_dictionary.domain.model.language

import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag

data class LanguageWordPreferences(
    val wordTypeTags: List<WordTypeTag>
)
