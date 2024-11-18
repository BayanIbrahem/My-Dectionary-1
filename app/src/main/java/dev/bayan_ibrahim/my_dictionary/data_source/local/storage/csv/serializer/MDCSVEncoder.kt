package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.nextOrNull

interface MDCSVEncoder<T> {
    val descriptor: MDCSVDescriptor
    val header: Collection<String>
    val encodedHeader: String
        get() = header.joinToString(descriptor.separator)

    fun encodeRawLine(value: T): String = descriptor.joinLine(encodeLine(value))

    fun encodeLine(value: T): Collection<String>

    fun encodeLines(
        iterator: Iterator<T>,
    ): List<String> {
        val lines = mutableListOf<String>()
        encodeLines(
            getValue = iterator::nextOrNull,
            onDecodeValidValue = lines::add
        )
        return lines
    }

}

inline fun <T> MDCSVEncoder<T>.encodeLines(
    getValue: () -> T?,
): List<String> {
    val lines = mutableListOf<String>()
    encodeLines(
        getValue = getValue,
        onDecodeValidValue = lines::add
    )
    return lines
}

inline fun <T> MDCSVEncoder<T>.encodeLines(
    iterator: Iterator<T>,
    onDecodeValidValue: (String) -> Boolean,
) {
    encodeLines(
        getValue = iterator::nextOrNull,
        onDecodeValidValue = onDecodeValidValue
    )
}

inline fun <T> MDCSVEncoder<T>.encodeLines(
    getValue: () -> T?,
    onDecodeValidValue: (String) -> Boolean,
) = with(descriptor) {
    var value = getValue()
    if (hasHeader) {
        onDecodeValidValue(joinLine(header))
    }
    while (value != null) {
        val continueEncoding = onDecodeValidValue(joinLine(encodeLine(value)))
        if (!continueEncoding) {
            break
        }
        value = getValue()
    }
}