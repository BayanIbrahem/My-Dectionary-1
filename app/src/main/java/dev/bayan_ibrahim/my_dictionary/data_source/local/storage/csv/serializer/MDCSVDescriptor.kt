package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer

class MDCSVDescriptor(
    val columnsCount: Int,
    val separator: String = DEFAULT_SEPARATOR,
    /**
     * set true if the file has a header
     */
    val hasHeader: Boolean = false,
    trimLeadingSpaces: Boolean = true,
    trimTrailingSpaces: Boolean = true,
    ignoreSpaceInSeparator: Boolean = true,
    ignoreExtraColumns: Boolean = true,
) {
    val separatorPattern = if (ignoreSpaceInSeparator) "\\s*${separator.trim()}\\s*".toRegex() else separator.toRegex()
    val trimLine: String.() -> String = if (trimLeadingSpaces && trimTrailingSpaces) {
        { this.trim() }
    } else if (trimLeadingSpaces) {
        { this.trimStart() }
    } else if (trimTrailingSpaces) {
        { this.trimEnd() }
    } else {
        { this }
    }
    val validCount: List<String>.() -> Boolean = if (columnsCount < 1) {
        { true }
    } else if (ignoreExtraColumns) {
        { this.count() >= columnsCount }
    } else {
        { this.count() == columnsCount }
    }


    companion object Companion {
        const val DEFAULT_SEPARATOR = ", "
    }

    fun splitLine(line: String): List<String> = line.split(separatorPattern)
    fun joinLine(line: Collection<String>): String = line.joinToString(separator)
}