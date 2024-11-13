package dev.bayan_ibrahim.my_dictionary.domain.model

import android.util.Log
import java.util.Locale

data class Language(
    val code: String,
    val selfDisplayName: String,
    val localDisplayName: String,
) {
    /**
     * code may be 2 or 3 chars this will be true if the code length is 3 this may be used
     * to make text smaller for viewing code
     */
    val isLongCode: Boolean = code.length > 2
    val fullDisplayName: String
        get() = if (selfDisplayName == localDisplayName) selfDisplayName else "$selfDisplayName - $localDisplayName"

    val valid = code.length in 2..3

    fun hasMatchQuery(query: String): Boolean = checkLanguagePartialMatchSearchQuery(this, query)
}

private val queryRegexMap: MutableMap<String, Regex> = mutableMapOf()

private fun checkLanguagePartialMatchSearchQuery(
    language: Language,
    query: String,
): Boolean {
    val queryRegex = queryRegexMap.getOrPut(query.trim().lowercase()) {
        query
            .trim()
            .lowercase()
            .toCharArray()
            .joinToString(
                separator = ".*",
                prefix = ".*",
                postfix = ".*"
            ).toRegex()
    }
    return sequenceOf(
        language.code,
        language.selfDisplayName,
        language.localDisplayName
    ).any {
        queryRegex.matches(it.lowercase()) // no need to trim since this values are not from user
    }
}

val allLanguages: Map<String, Language> by lazy {
    val defaultLocale = Locale.getDefault()
    Locale.getAvailableLocales().associate { locale ->
        locale.language to Language(
            code = locale.language,
            selfDisplayName = locale.displayLanguage,
            localDisplayName = locale.getDisplayLanguage(defaultLocale)
        )
    }.also {
        Log.d("language", "lazy builder value, $it")
    }
}

