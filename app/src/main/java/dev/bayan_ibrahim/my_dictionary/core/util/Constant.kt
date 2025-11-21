package dev.bayan_ibrahim.my_dictionary.core.util

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import kotlinx.datetime.Instant

val INVALID_INSTANT = Instant.fromEpochSeconds(0)
const val INVALID_ID = -1L
const val INVALID_TEXT = ""
val INVALID_LANGUAGE = Language(
    code = "",
    selfDisplayName = "",
    localDisplayName = ""
)

/**
 * return null if the value is less than 0
 */
@JvmName("LongNullIfInvalid")
fun Long.nullIfNegative() = this.takeUnless { it < 0 }

@JvmName("StringNullIfInvalid")
fun String.nullIfInvalid() = nullIfInvalid(INVALID_TEXT)

@JvmName("TNullIfInvalid")
fun <T : Any> T.nullIfInvalid(invalidValue: T): T? = if (this == invalidValue) null else this


@JvmName("LongInvalidIfNull")
fun Long?.invalidIfNull() = invalidIfNull(INVALID_ID)

@JvmName("StringInvalidIfNull")
fun String?.invalidIfNull() = invalidIfNull(INVALID_ID)

@JvmName("TInvalidIfNull")
fun <T : Any> T?.invalidIfNull(invalidValue: T): T = this ?: invalidValue
