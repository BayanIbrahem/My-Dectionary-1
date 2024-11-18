@file:Suppress("BlockingMethodInNonBlockingContext")

package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.MDCSVSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.decodeLines
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import java.io.InputStream

class MDCSVFileReader<Data : Any>(
    private val serializer: MDCSVSerializer<Data>,
    override val openInputStream: (MDFileData) -> InputStream?,
    children: List<MDFileReaderDecorator<Data>> = emptyList(),
) : MDFileReaderDecorator<Data>(
    supportedMimeTypes = setOf("text/csv"),
    openInputStream = openInputStream,
    children = children,
) {
    override val label: String = "CSV"

    /**
     * @throws [IllegalArgumentException] if the file is empty
     * @throws [MDCSVSerializer.decodeLine] if there is any problem in serialization
     */
    override suspend fun tryParseFileHeader(stream: InputStream) {
        val line: String? = stream.bufferedReader().readLine()
        require(line != null) { "Trying to read an empty file" }

        serializer.decodeLine(line)
    }

    override suspend fun parseInputStream(
        stream: InputStream,
        onReadItem: suspend (Data) -> Boolean,
    ) {
        stream.bufferedReader().use { reader ->
            serializer.decodeLines(
                getLine = {
                    val line = reader.readLine()
                    line
                },
                onDecodeValidColumns = {
                    onReadItem(it)
                },
            )
        }
    }

}