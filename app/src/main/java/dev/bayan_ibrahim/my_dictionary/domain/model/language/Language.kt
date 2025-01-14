package dev.bayan_ibrahim.my_dictionary.domain.model.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryRegexNormalize
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable(LanguageSerializer::class)
open class Language(
    code: String,
    val selfDisplayName: String,
    val localDisplayName: String,
) : LanguageCode(code) {
    val direction: LayoutDirection
        get() = if (code in rtlLanguagesCodes) LayoutDirection.Rtl else LayoutDirection.Ltr

    /**
     * @throws [NullPointerException] if [code] is not a valid code or doesn't exist in [allLanguages]
     */
    constructor(code: String) : this(
        code = code,
        selfDisplayName = allLanguages[code.code]!!.selfDisplayName,
        localDisplayName = allLanguages[code.code]!!.localDisplayName
    )

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
    val queryRegex = queryRegexCacheMap.getOrPut(query.meaningSearchNormalize) {
        query.searchQueryRegexNormalize.toRegex()
    }
    return sequenceOf(
        language.code,
        language.selfDisplayName,
        language.localDisplayName
    ).any {
        queryRegex.matches(it.lowercase()) // no need to trim since this values are not from user
    }
}

fun <C : LanguageCode> C.getLanguage(): Language = allLanguages[this]!!
fun <C : LanguageCode> C.getLanguageWordSpace(wordsCount: Int = 0): LanguageWordSpace = LanguageWordSpace(code, wordsCount)

val defaultLanguage: Language
    get() = Locale.getDefault().language.code.getLanguage()

val rtlLanguagesCodes by lazy {
    setOf("ar", "fa", "he", "iw", "ks", "ku", "ur", "ckb", "lrc")
}
val allLanguages: Map<out LanguageCode, Language> by lazy {
    val defaultLocale = Locale.getDefault()
    Locale.getAvailableLocales().associate { locale ->
        locale.language.code to Language(
            code = locale.language,
            selfDisplayName = locale.getDisplayLanguage(locale),
            localDisplayName = locale.getDisplayLanguage(defaultLocale),
        )
    }
}