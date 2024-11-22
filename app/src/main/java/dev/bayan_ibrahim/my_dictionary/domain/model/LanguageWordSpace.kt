package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_LANGUAGE

data class LanguageWordSpace(
    val language: Language = INVALID_LANGUAGE,
    val wordsCount: Int = 0,
    val averageLearningProgress: Float = 0f,
) {
    val valid: Boolean get() = language.validCode
}

