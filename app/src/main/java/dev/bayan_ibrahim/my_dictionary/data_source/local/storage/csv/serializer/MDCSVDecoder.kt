package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.nextOrNull

interface MDCSVDecoder<T> {
    val descriptor: MDCSVDescriptor

    fun decodeLine(line: String): T = decodeLine(descriptor.splitLine(line))

    fun decodeLine(line: List<String>): T

    fun decodeLines(
        iterator: Iterator<String>,
        ignoreInvalidLines: Boolean = true,
    ): List<T> {
        return decodeLines(
            ignoreInvalidLines = ignoreInvalidLines,
            getLine = iterator::nextOrNull,
        )
    }
}

inline fun <T> MDCSVDecoder<T>.decodeLines(
    getLine: () -> String?,
    ignoreInvalidLines: Boolean = true,
): List<T> {
    val items = mutableListOf<T>()
    decodeLines(
        ignoreInvalidLines = ignoreInvalidLines,
        getLine = getLine,
        onDecodeValidColumns = {
            items.add(it)
            true
        }
    )
    return items
}

inline fun <T> MDCSVDecoder<T>.decodeLines(
    iterator: Iterator<String>,
    onDecodeValidColumns: (T) -> Boolean,
    ignoreInvalidLines: Boolean = true,
) {
    return decodeLines(
        ignoreInvalidLines = ignoreInvalidLines,
        getLine = iterator::nextOrNull,
        onDecodeValidColumns = onDecodeValidColumns
    )
}

inline fun <T> MDCSVDecoder<T>.decodeLines(
    getLine: () -> String?,
    onDecodeValidColumns: (T) -> Boolean,
    ignoreInvalidLines: Boolean = true,
) = with(descriptor) {
    var line = getLine()?.trimLine()
    if (hasHeader) {
        // ignore first line
        line = getLine()?.trimLine()
    }
    while (line != null) {
        val columns = splitLine(line)
        val validCount = columns.validCount()
        if (validCount) {
            val decodedLine = decodeLine(columns)
            val continueParsing = onDecodeValidColumns(decodedLine)
            if (!continueParsing) {
                break
            }
        } else if (!ignoreInvalidLines) {
            throw IllegalArgumentException("Invalid columns count expected: $columnsCount actual: ${columns.count()}")
        }
        line = getLine()?.trimLine()
    }
}
