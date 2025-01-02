package dev.bayan_ibrahim.my_dictionary.domain.model.language

open class LanguageWordSpace(
    code: String,
    val wordsCount: Int = 0,
) : Language(code)

