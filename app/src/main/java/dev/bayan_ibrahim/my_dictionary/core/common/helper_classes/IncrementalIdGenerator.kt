package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

class IncrementalIdGenerator(
    private val initialValue: Long = 0L,
) {
    var id: Long = initialValue
        private set

    fun nextId(): Long {
        id++
        return id
    }

    fun reset(): Long {
        id = initialValue
        return initialValue
    }
}