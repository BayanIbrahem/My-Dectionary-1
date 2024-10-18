package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods


/**
 * clear, then add all data
 */
@JvmName("MutableListSetAll")
fun <T> MutableList<T>.setAll(items: Collection<T>) {
    clear()
    addAll(items)
}

/**
 * clear, then put all data
 */
@JvmName("MutableMapSetAll")
fun <K, V> MutableMap<K, V>.setAll(items: Map<K, V>) {
    clear()
    putAll(items)
}
