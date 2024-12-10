package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.minN

import kotlin.math.log

/**
 * getting n min item of this collection either by building new sub list using [minNByIterativeMin], or
 * sorting the large list then take a sublist in it using [minNBySorting]
 */
inline fun <T, R : Comparable<R>> Collection<T>.minNBy(
    minCount: Int,
    crossinline selector: (T) -> R,
): List<T> {
    val count = count()
    if (count <= minCount) return toList()
    return if (minCount <= log(count.toDouble(), 2.0)) { // searching for each min is more efficient
        minNByIterativeMin(minCount = minCount, selector = selector)
    } else { // sorting all the list is more efficient
        minNBySorting(minCount = minCount, selector = selector)
    }
}

/**
 * sort all the list then take a sublist[0..[minCount]] faster than [minNByIterativeMin] if [minCount] is
 * larger than log2(count())
 * @see [minNBy]
 */
inline fun <R : Comparable<R>, T> Collection<T>.minNBySorting(
    minCount: Int,
    crossinline selector: (T) -> R,
) = sortedBy {
    selector(it)
}.subList(0, minCount)

/**
 * iterative picking the min n items in this collection, faster than [minNBySorting] if [minCount] is
 * smaller than log2(count())
 * @see [minNBy]
 */
inline fun <R : Comparable<R>, T> Collection<T>.minNByIterativeMin(
    minCount: Int,
    selector: (T) -> R,
): MutableList<T> {
    val results = mutableListOf<T>()
    val selectedIndexes = mutableSetOf<Int>()
    repeat(minCount) {
        var min: T? = null
        var minI: Int? = null
        var minSelector: R? = null
        forEachIndexed { i, item ->
            if (i !in selectedIndexes) {
                val currentSelector = selector(item)
                if (minSelector == null || minSelector!! > currentSelector) {
                    min = item
                    minI = i
                    minSelector = currentSelector
                }
            }
        }
        results.add(min!!)
        selectedIndexes.add(minI!!)
    }
    return results
}
