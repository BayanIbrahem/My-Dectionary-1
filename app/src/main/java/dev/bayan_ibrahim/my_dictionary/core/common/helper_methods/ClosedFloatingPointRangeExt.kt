package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

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
    require(inputRangeStart != inputRangeEnd) { "invalid input range $inputRangeStart..$inputRangeEnd" }
    if (outputRangeStart == outputRangeEnd) return outputRangeStart
    // y = mx
    val percentInInput = input.minus(inputRangeStart).div(inputRangeEnd - inputRangeStart)
    val outputRange = outputRangeEnd.minus(outputRangeStart)
    return percentInInput.times(outputRange).plus(outputRangeStart)
}

