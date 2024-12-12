package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.asEpochMillisecondsInstant() = Instant.fromEpochMilliseconds(this)
fun Long.asEpochSecondsInstant() = Instant.fromEpochSeconds(this)

fun Instant.toDefaultLocalDateTime(): LocalDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
