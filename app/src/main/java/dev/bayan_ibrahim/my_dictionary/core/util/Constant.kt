package dev.bayan_ibrahim.my_dictionary.core.util

import dev.bayan_ibrahim.my_dictionary.domain.model.Language

const val INVALID_ID = -1L
const val INVALID_TEXT = ""
val INVALID_LANGUAGE = Language("", "", "")

@JvmName("LongNullIfInvalid")
fun Long.nullIfInvalid() = nullIfInvalid(INVALID_ID)

@JvmName("StringNullIfInvalid")
fun String.nullIfInvalid() = nullIfInvalid(INVALID_TEXT)

@JvmName("TNullIfInvalid")
fun <T : Any> T.nullIfInvalid(invalidValue: T): T? = if (this == invalidValue) null else this


@JvmName("LongInvalidIfNull")
fun Long?.invalidIfNull() = invalidIfNull(INVALID_ID)

@JvmName("StringInvalidIfNull")
fun String?.invalidIfNull() = invalidIfNull(INVALID_ID)

@JvmName("TInvalidIfNull")
fun <T : Any> T?.invalidIfNull(invalidValue: T) = this ?: invalidValue
