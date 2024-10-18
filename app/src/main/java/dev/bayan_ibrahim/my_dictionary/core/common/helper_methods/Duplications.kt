package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

fun <T> Collection<T>.getDuplicatedItems(): List<Set<Int>> {
    val items = mutableMapOf<T, MutableSet<Int>>()
    forEachIndexed { index, t ->
        items.getOrPut(t) { mutableSetOf() }.add(index)
    }
    return items.values.toList()
}
