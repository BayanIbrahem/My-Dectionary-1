package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

import kotlin.math.floor

fun ClosedFloatingPointRange<Float>.calculateOutput(
    input: Float,
    forOutputRange: ClosedFloatingPointRange<Float>,
): Float = calculateOutput(
    input = input,
    inputRangeStart = this.start,
    inputRangeEnd = this.endInclusive,
    outputRangeStart = forOutputRange.start,
    outputRangeEnd = forOutputRange.endInclusive
)

fun calculateOutput(
    input: Float,
    inputRangeStart: Float,
    inputRangeEnd: Float,
    outputRangeStart: Float,
    outputRangeEnd: Float,
): Float {
    val slope = (outputRangeEnd - outputRangeStart) / (inputRangeEnd - inputRangeStart)
    return outputRangeStart + floor(slope * (input - inputRangeStart) + 0.5f)
}

