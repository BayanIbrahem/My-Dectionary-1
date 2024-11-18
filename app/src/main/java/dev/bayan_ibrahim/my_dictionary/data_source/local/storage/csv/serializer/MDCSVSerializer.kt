package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.MDCSVDescriptor.Companion.DEFAULT_SEPARATOR

abstract class MDCSVSerializer<T>(
    override val descriptor: MDCSVDescriptor,
    override val header: Collection<String> = List(descriptor.columnsCount) {
        it.toString()
    },
) : MDCSVDecoder<T>, MDCSVEncoder<T> {
    constructor(
        /**
         * if column count smaller than 1 then all columns would be taken and the option [ignoreExtraColumns] will be ignored (always false)
         */
        columnsCount: Int = 0,
        separator: String = DEFAULT_SEPARATOR,
        trimLeadingSpaces: Boolean = true,
        trimTrailingSpaces: Boolean = true,
        ignoreSpaceInSeparator: Boolean = true,
        ignoreExtraColumns: Boolean = true,
        header: Collection<String> = List(columnsCount) {
            it.toString()
        },
    ) : this(
        descriptor = MDCSVDescriptor(
            columnsCount = columnsCount,
            separator = separator,
            trimLeadingSpaces = trimLeadingSpaces,
            trimTrailingSpaces = trimTrailingSpaces,
            ignoreSpaceInSeparator = ignoreSpaceInSeparator,
            ignoreExtraColumns = ignoreExtraColumns,
        ),
        header = header
    )
}

class MDCSVListSerializer<T>(
    private val decoder: (String) -> T,
    private val encoder: (T) -> String,
    separator: String = DEFAULT_SEPARATOR,
    trimLeadingSpaces: Boolean = true,
    trimTrailingSpaces: Boolean = true,
    ignoreSpaceInSeparator: Boolean = true,
) : MDCSVSerializer<List<T>>(
    columnsCount = 0,
    separator = separator,
    trimLeadingSpaces = trimLeadingSpaces,
    trimTrailingSpaces = trimTrailingSpaces,
    ignoreSpaceInSeparator = ignoreSpaceInSeparator,
) {
    override fun decodeLine(line: List<String>) = line.map(decoder)
    override fun encodeLine(value: List<T>) = value.map(encoder)

    companion object Companion {
        fun stringListSerializer(
            separator: String = DEFAULT_SEPARATOR,
            trimLeadingSpaces: Boolean = true,
            trimTrailingSpaces: Boolean = true,
            ignoreSpaceInSeparator: Boolean = true,
        ): MDCSVListSerializer<String> = MDCSVListSerializer(
            decoder = { it },
            encoder = { it },
            separator = separator,
            trimLeadingSpaces = trimLeadingSpaces,
            trimTrailingSpaces = trimTrailingSpaces,
            ignoreSpaceInSeparator = ignoreSpaceInSeparator
        )

    }
}

