package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods


fun <V> List<V>.safeSubList(
    fromIndex: Int = 0,
    toIndex: Int = this.size,
): List<V> = this.subList(fromIndex, minOf(toIndex, this.size))