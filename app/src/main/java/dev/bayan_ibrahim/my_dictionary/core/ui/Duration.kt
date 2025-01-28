package dev.bayan_ibrahim.my_dictionary.core.ui

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


fun Duration.toFormattedString(): String {
    if (this < 1.seconds) {
        return "0.${inWholeMilliseconds}ms"
    }
    if (this < 1.minutes) {
        return "${inWholeSeconds}s"
    } else if (this < 1.hours) {
        return "${inWholeMinutes}m:${inWholeSeconds % 60}s"
    }
    return toIsoString().substring(2)
}