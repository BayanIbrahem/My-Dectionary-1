package dev.bayan_ibrahim.my_dictionary.domain.model

import java.util.Locale

data class Language(
    val code: String,
    val selfDisplayName: String,
    val localDisplayName: String,
) {
    val fullDisplayName: String
        get() = if (selfDisplayName == localDisplayName) selfDisplayName else "$selfDisplayName - $localDisplayName"

    fun matchQuery(query: String): Boolean = checkLanguageFullMatchSearchQuery(this, query)
    fun hasMatchQuery(query: String): Boolean = checkLanguagePartialMatchSearchQuery(this, query)
}

fun checkLanguageFullMatchSearchQuery(
    language: Language,
    query: String,
): Boolean = checkLanguageMatchSearchQuery(
    language = language,
    query = query,
    selector = String::equals
)

fun checkLanguagePartialMatchSearchQuery(
    language: Language,
    query: String,
): Boolean = checkLanguageMatchSearchQuery(
    language = language,
    query = query,
    selector = String::contains
)

private fun checkLanguageMatchSearchQuery(
    language: Language,
    query: String,
    selector: String.(String) -> Boolean,
): Boolean {
    val normalizedQuery = query.trim().lowercase()
    return sequenceOf(
        language.code,
        language.selfDisplayName,
        language.localDisplayName
    ).any { it.selector(normalizedQuery) }
}

val allLanguages: Map<String, Language> by lazy {
    val defaultLocale = Locale.getDefault()
    Locale.getAvailableLocales().associate { locale ->
        locale.language to Language(
            code = locale.language,
            selfDisplayName = locale.displayLanguage,
            localDisplayName = locale.getDisplayLanguage(defaultLocale)
        )
    }
}

