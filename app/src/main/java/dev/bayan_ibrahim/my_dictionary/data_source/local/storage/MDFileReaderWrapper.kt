package dev.bayan_ibrahim.my_dictionary.data_source.local.storage

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import java.io.InputStream

class MDFileReaderWrapper<Data : Any>(
    override val openInputStream: (MDFileData) -> InputStream?,
    override val children: List<MDFileReaderDecorator<Data>>,
) : MDFileReaderDecorator<Data>(
    supportedMimeTypes = emptySet(),
    openInputStream
) {
    override val label: String = "Wrapper"

    override suspend fun tryParseFileHeader(stream: InputStream) {
        throw wrapperException
    }

    override suspend fun parseInputStream(stream: InputStream, onReadItem: suspend (Data) -> Boolean) {
        throw wrapperException
    }
}

private val wrapperException =
    IllegalArgumentException("Wrapper can not parse any input stream, if this exception thrown, that means you tried to read a stream without passing suitable children that can handle this stream input")