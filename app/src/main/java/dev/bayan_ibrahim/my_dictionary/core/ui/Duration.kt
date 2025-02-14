package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


val Duration.format: String
    @Composable
    @ReadOnlyComposable
    get() = if (this < 1.seconds) {
        stringResource(R.string.time_ms, inWholeMilliseconds)
    } else if (this < 1.minutes) {
        stringResource(R.string.time_s, inWholeSeconds)
    } else if (this < 1.hours) {
        stringResource(R.string.time_m_s, inWholeMinutes, inWholeSeconds % 60)
    } else {
        stringResource(R.string.time_h_m_s, inWholeHours, inWholeMinutes % 60, inWholeSeconds % 60)
    }