package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

import kotlin.time.Duration
import kotlin.time.DurationUnit


val Duration.inDays: Float
    get() = this.toDouble(DurationUnit.DAYS).toFloat()


val Duration.inHours: Float
    get() = toDouble(DurationUnit.HOURS).toFloat()


val Duration.inMinutes: Float
    get() = toDouble(DurationUnit.MINUTES).toFloat()


val Duration.inSeconds: Float
    get() = toDouble(DurationUnit.SECONDS).toFloat()


val Duration.inMilliseconds: Float
    get() = toDouble(DurationUnit.MILLISECONDS).toFloat()

val Duration.inMicroseconds: Float
    get() = toDouble(DurationUnit.MICROSECONDS).toFloat()


val Duration.inNanoseconds: Float
    get() = toDouble(DurationUnit.NANOSECONDS).toFloat()
