package dev.bayan_ibrahim.my_dictionary.domain.model.language

import kotlinx.serialization.Serializable


val String.code: LanguageCode get() = LanguageCode(code = this)

private val languageCodeRegex = "[a-z]{2,3}".toRegex()

@Serializable(LanguageCodeSerializer::class)
open class LanguageCode(code: String) {
    val code: String = code.lowercase()

    val validLength: Boolean
        get() = code.length in 2..3

    val valid: Boolean
        get() = languageCodeRegex.matches(code)

    val isLongCode: Boolean
        get() = code.length > 2

    val lowercaseCode: String
        get() = code
    val uppercaseCode: String
        get() = code.uppercase()
    val capitalizedCode: String
        get() = code.replaceFirstChar { it.uppercaseChar() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LanguageCode) return false

        if (code != other.code) return false

        return true
    }


    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun toString(): String {
        return "LanguageCode(code='$code', valid=$valid)"
    }

}

