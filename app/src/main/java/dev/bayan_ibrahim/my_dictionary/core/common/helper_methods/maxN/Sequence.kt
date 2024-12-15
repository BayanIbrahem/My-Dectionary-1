package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.maxN

import kotlin.math.log

/**
 * getting n max item of this collection either by building new sub list using [maxNByIterativeMax], or
 * sorting the large list then take a sublist in it using [maxNBySorting]
 */
inline fun <T, R : Comparable<R>> Sequence<T>.maxNBy(
    maxCount: Int,
    crossinline selector: (T) -> R,
): List<T> {
    val count = count()
    if (count <= maxCount) return toList()
    return if (maxCount <= log(count.toDouble(), 2.0)) { // searching for each max is more efficient
        maxNByIterativeMax(maxCount = maxCount, selector = selector)
    } else { // sorting all the list is more efficient
        maxNBySorting(maxCount = maxCount, selector = selector)
    }
}

/**
 * sort all the list then take a sublist[0..[maxCount]] faster than [maxNByIterativeMax] if [maxCount] is
 * larger than log2(count())
 * @throws NoSuchElementException if [maxCount] is larger than the size of current list.
 * @see [maxNBy]
 */
inline fun <R : Comparable<R>, T> Sequence<T>.maxNBySorting(
    maxCount: Int,
    crossinline selector: (T) -> R,
) = sortedByDescending {
    selector(it)
}.iterator().let { iterator ->
    val subList = mutableListOf<T>()
    repeat(maxCount) {
        subList.add(iterator.next())
    }
    subList
}

/**
 * iterative picking the max n items in this collection, faster than [maxNBySorting] if [maxCount] is
 * smaller than log2(count())
 * @see [maxNBy]
 */
inline fun <R : Comparable<R>, T> Sequence<T>.maxNByIterativeMax(
    maxCount: Int,
    selector: (T) -> R,
): MutableList<T> {
    val results = mutableListOf<T>()
    val selectedIndexes = mutableSetOf<Int>()
    repeat(maxCount) {
        var max: T? = null
        var maxI: Int? = null
        var maxSelector: R? = null
        forEachIndexed { i, item ->
            if (i !in selectedIndexes) {
                val currentSelector = selector(item)
                if (maxSelector == null || maxSelector!! < currentSelector) {
                    max = item
                    maxI = i
                    maxSelector = currentSelector
                }
            }
        }
        results.add(max!!)
        selectedIndexes.add(maxI!!)
    }
    return results
}
