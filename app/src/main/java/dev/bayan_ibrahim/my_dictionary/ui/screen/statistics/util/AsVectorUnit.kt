package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

fun Offset.asVectorUnit(): Offset {
    if (this == Offset.Zero) return this
    val factor = 1 / maxOf(abs(x), abs(y))
    return Offset(x / factor, y / factor)
}
