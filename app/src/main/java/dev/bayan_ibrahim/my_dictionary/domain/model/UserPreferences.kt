package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

data class UserPreferences(
    val selectedLanguagePage: Language?,
)