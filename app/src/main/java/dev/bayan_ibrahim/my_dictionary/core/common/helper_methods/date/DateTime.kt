package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.Companion.now() = Clock.System.now().toDefaultLocalDateTime()

