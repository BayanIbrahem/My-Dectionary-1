package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

/**
 * apply or operation on two setts and return all items if it is in first or in second set
 */
infix fun <T> Set<T>.or(other: Set<T>): Set<T> {
    return this + other
}

/**
 * apply and operation on two setts and return all items if it is in first and in second set
 */
infix fun <T> Set<T>.and(other: Set<T>): Set<T> = this.filter { it in other }.toSet()

// TODO, fix performance
inline fun <T> Set<T>.andBy(
    other: Set<T>,
    selector: (T, T) -> Boolean,
): Set<T> = this.filter { item ->
    other.any { selector(it, item) }
}.toSet()
