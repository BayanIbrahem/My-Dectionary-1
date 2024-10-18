package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

fun <T> MutableList<T>.distinct() {
    val items = mutableSetOf<T>()
    forEach { item ->
        if (item in items) {
            this.remove(item)
        } else {
            items.add(item)
        }
    }
}
