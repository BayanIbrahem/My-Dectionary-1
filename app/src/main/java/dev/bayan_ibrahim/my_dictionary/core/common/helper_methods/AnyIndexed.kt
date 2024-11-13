package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

fun <T> Iterable<T>.anyIndexed(
    predicate: (Int, T) -> Boolean,
): Boolean {
    forEachIndexed { i, it ->
        if (predicate(i, it)) return true
    }
    return false
}

fun <T> Iterable<T>.allIndexed(
    predicate: (Int, T) -> Boolean,
): Boolean {
    forEachIndexed { i, it ->
        if (!predicate(i, it)) return false
    }
    return true
}
