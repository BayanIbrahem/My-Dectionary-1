package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.minN

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.nextOrNull
import kotlin.math.log

/**
 * getting n min dev.bayan_ibrahim.my_dictionary.core.design_system.group.item of this collection either by building new sub list using [minNByIterativeMin], or
 * sorting the large list then take a sublist in it using [minNBySorting]
 */
inline fun <T, R : Comparable<R>> Sequence<T>.minNBy(
    maxCount: Int,
    crossinline selector: (T) -> R,
): List<T> {
    val count = count()
    if (count <= maxCount) return toList()
    return if (maxCount <= log(count.toDouble(), 2.0)) { // searching for each min is more efficient
        minNByIterativeMin(maxCount = maxCount, selector = selector)
    } else { // sorting all the list is more efficient
        minNBySorting(maxCount = maxCount, selector = selector)
    }
}

/**
 * sort all the list then take a sublist[0..[maxCount]] faster than [minNByIterativeMin] if [maxCount] is
 * larger than log2(count())
 * @throws NoSuchElementException if [maxCount] is larger than the size of current list.
 * @see [minNBy]
 */
inline fun <R : Comparable<R>, T> Sequence<T>.minNBySorting(
    maxCount: Int,
    crossinline selector: (T) -> R,
) = sortedBy {
    selector(it)
}.iterator().subList(maxCount)

fun <T> Iterator<T>.subList(maxCount: Int): List<T> {
    val subList = mutableListOf<T>()
    var i = 0
    while (i < maxCount) {
        val next = nextOrNull() ?: break
        subList.add(next)
        i++
    }
    return subList
}

/**
 * iterative picking the min n items in this collection, faster than [minNBySorting] if [maxCount] is
 * smaller than log2(count())
 * @see [minNBy]
 */
inline fun <R : Comparable<R>, T> Sequence<T>.minNByIterativeMin(
    maxCount: Int,
    selector: (T) -> R,
): MutableList<T> {
    val results = mutableListOf<T>()
    val selectedIndexes = mutableSetOf<Int>()
    repeat(maxCount) {
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
