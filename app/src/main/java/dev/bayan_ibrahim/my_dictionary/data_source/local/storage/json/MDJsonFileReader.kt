package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import java.io.IOException
import java.io.InputStream


class MDJsonFileReader<Data : Any>(
    /**
     * just use [Json.decodeToSequence] for this param
     */
    private val decodeToSequence: suspend (InputStream) -> Sequence<Data>,
    override val openInputStream: (MDFileData) -> InputStream?,
    children: List<MDFileReaderDecorator<Data>> = emptyList(),
) : MDFileReaderDecorator<Data>(
    supportedMimeTypes = setOf("application/json"),
    openInputStream = openInputStream,
    children = children
) {
    override val label: String = "Json"

    /**
     * @throws NoSuchElementException if the sequence is empty.
     * @throws [SerializationException] if the given JSON input cannot be deserialized to the value
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance
     * @throws [IOException] If an I/O error occurs and stream cannot be read from.
     */
    override suspend fun tryParseFileHeader(stream: InputStream) {
        decodeToSequence(stream).first()
    }

    override suspend fun parseInputStream(
        stream: InputStream,
        onReadItem: suspend (Data) -> Boolean,
    ) {
        decodeToSequence(stream).forEach { word ->
            onReadItem(word)
        }
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        inline fun <reified Data : Any> build(
            json: Json,
            noinline openInputStream: (MDFileData) -> InputStream?,
            children: List<MDFileReaderDecorator<Data>> = emptyList(),
        ) = MDJsonFileReader(
            decodeToSequence = json::decodeToSequence,
            openInputStream = openInputStream,
            children = children,
        )
    }
}




