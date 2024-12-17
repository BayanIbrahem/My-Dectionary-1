package dev.bayan_ibrahim.my_dictionary.domain.model

import kotlin.time.Duration

sealed interface MDTimer {
    val duration: Duration
    val completed: Boolean

    data class OneShot(
        override val duration: Duration = Duration.INFINITE,
        override val completed: Boolean,
    ) : MDTimer

    data class FixedRate(
        override val duration: Duration = Duration.INFINITE,
        override val completed: Boolean,
        val rate: Duration,
        val passedDuration: Duration,
    ) : MDTimer
}