package dev.bayan_ibrahim.my_dictionary.domain.model

data class LanguageWordSpace(
    val language: Language,
    val wordsCount: Int = 0,
    val averageLearningProgress: Float = 0f,
)