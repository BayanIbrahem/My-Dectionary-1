package dev.bayan_ibrahim.my_dictionary.domain.model.language

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable(LanguageSerializer::class)
data class Language(
    val code: LanguageCode,
    val selfDisplayName: String,
    val localDisplayName: String,
) {
    val validCode: Boolean
        get() = code.valid

    val validLanguage: Boolean
        get() = validCode && code in allLanguages

    /**
     * code may be 2 or 3 chars this will be true if the code length is 3 this may be used
     * to make text smaller for viewing code
     */
    val fullDisplayName: String
        get() = if (selfDisplayName == localDisplayName) selfDisplayName else "$selfDisplayName - $localDisplayName"

    fun hasMatchQuery(query: String): Boolean = checkLanguagePartialMatchSearchQuery(this, query)

}

private val queryRegexCacheMap: MutableMap<String, Regex> = mutableMapOf()

private fun checkLanguagePartialMatchSearchQuery(
    language: Language,
    query: String,
): Boolean {
    val queryRegex = queryRegexCacheMap.getOrPut(query.trim().lowercase()) {
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
        language.code.code,
        language.selfDisplayName,
        language.localDisplayName
    ).any {
        queryRegex.matches(it.lowercase()) // no need to trim since this values are not from user
    }
}

@get:JvmName("LanguageCode_language")
val LanguageCode.language: Language
    get() = allLanguages[this]!!

val LanguageCode.languageOrNull: Language?
    get() = allLanguages[this]

@get:JvmName("LanguageCode_wordSpace")
val LanguageCode.wordSpace: LanguageWordSpace
    get() = LanguageWordSpace(language)

@get:JvmName("Language_wordSpace")
val Language.wordSpace: LanguageWordSpace
    get() = LanguageWordSpace(this)

val allLanguages: Map<LanguageCode, Language> by lazy {
    val defaultLocale = Locale.getDefault()
    Locale.getAvailableLocales().associate { locale ->
        locale.language.code to Language(
            code = locale.language.code,
            selfDisplayName = locale.displayLanguage,
            localDisplayName = locale.getDisplayLanguage(defaultLocale)
        )
    }
}

