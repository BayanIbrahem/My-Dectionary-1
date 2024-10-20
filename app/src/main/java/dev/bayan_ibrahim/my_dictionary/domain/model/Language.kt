package dev.bayan_ibrahim.my_dictionary.domain.model

import java.util.Locale

data class Language(
    val code: String,
    val selfDisplayName: String,
    val localDisplayName: String,
) {
    val fullDisplayName: String
        get() = if (selfDisplayName == localDisplayName) selfDisplayName else "$selfDisplayName - $localDisplayName"
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