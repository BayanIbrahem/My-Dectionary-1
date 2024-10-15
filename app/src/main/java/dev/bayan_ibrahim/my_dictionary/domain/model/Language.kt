package dev.bayan_ibrahim.my_dictionary.domain.model

import java.util.Locale

data class Language(
    val code: String,
    val selfDisplayName: String,
    val localDisplayName: String,
)

val allLanguages by lazy {
    val defaultLocale = Locale.getDefault()
    Locale.getAvailableLocales().map { locale ->
        Language(
            code = locale.language,
            selfDisplayName = locale.displayLanguage,
            localDisplayName = locale.getDisplayLanguage(defaultLocale)
        )
    }
}