package dev.bayan_ibrahim.my_dictionary.core.design_system.chart.chart_util

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput

/**
 * calculate y position value from range 0..1 for each chart value according to y labels,
 * since y labels values are not distributed equally but using [yPositionOfValue]
 * if you aren't using [yPositionOfValue] then this results would be incorrect
 * @return map of float position from range 0..1 for any value to use it just map the map value from
 * 0..1 to the minimum and maximum y axis value in chart
 */
fun calculateYPositionsForValues(
    values: List<Int>,
    yLabelsValues: List<Int>,
): Map<Int, Float> {
    val count = yLabelsValues.count()
    val positionMap = mutableMapOf<Int, Float>()
    values.forEach { value ->
        positionMap.getOrPut(value) {
            // sorted desc so the max value would have the minimum y axis
            yPositionOfValue(yLabelsValues = yLabelsValues, value = value, count = count)
        }
    }
    return positionMap
}

private fun yPositionOfValue(yLabelsValues: List<Int>, value: Int, count: Int): Float {
    val largestMin = yLabelsValues.indexOfFirst {
        // there is always a value this is larger than the current so we choose <= instead of <
        it <= value
    }
    val smallestMax = largestMin.dec()
    val largestMinOutput = calculateYOutput(
        index = largestMin,
        count = count,
        top = 0f,
        bottom = 100f,
    )
    val smallestMaxOutput = calculateYOutput(
        index = smallestMax,
        count = count,
        top = 0f,
        bottom = 100f,
    )

    val output = calculateOutput(
        input = value.toFloat(),
        inputRangeStart = yLabelsValues[largestMin].toFloat(),
        inputRangeEnd = yLabelsValues[smallestMax].toFloat(),
        outputRangeStart = largestMinOutput,
        outputRangeEnd = smallestMaxOutput,
    )
    return 1f - output / 100f
}