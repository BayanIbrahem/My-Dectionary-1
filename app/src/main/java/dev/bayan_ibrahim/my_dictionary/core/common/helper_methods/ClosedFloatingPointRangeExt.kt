package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

@JvmName("FloatCalculateOutput")
fun Float.calculateOutput(inputStart: Float, inputEnd: Float, outputStart: Float, outputEnd: Float): Float = calculateOutput(
    input = this,
    inputRangeStart = inputStart,
    inputRangeEnd = inputEnd,
    outputRangeStart = outputStart,
    outputRangeEnd = outputEnd
)
@JvmName("ClosedFloatingPointRangeOfFloatCalculateOutput")
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


/**
 * return percent of input range
 * @param percent if 0 return [start] if 1 return [end] if between 0 and 1 return add [percent] value of range length to [start]
```kotlin
val start =  10f; val end = 20f
val p1 = 0.2f // result: 10 + 0.2 *(20-10) = 12
val p2 = 0.5f // result: 10 + 0.5 *(20-10) = 15
val p2 = 1.5f // result: 10 + 1.5 *(20-10) = 25
val p2 = -0.5f // result: 10 - 0.5 *(20-10) = 5
```
 */
fun percentInRange(
    percent: Float,
    start: Float,
    end: Float,
): Float {
    val length = end - start
    val delta = length * percent
    return start + delta
}