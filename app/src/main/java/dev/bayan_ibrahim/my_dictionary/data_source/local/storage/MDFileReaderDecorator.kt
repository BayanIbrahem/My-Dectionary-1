package dev.bayan_ibrahim.my_dictionary.data_source.local.storage

import android.util.Log
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.exception.CloseTransactionException
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import java.io.InputStream

abstract class MDFileReaderDecorator<Data : Any>(
    val supportedMimeTypes: Set<String>,
    protected open val openInputStream: (MDFileData) -> InputStream?,
    open val children: List<MDFileReaderDecorator<Data>> = emptyList(),
) {
    abstract val label: String

    /**
     * @return true if this reader or any of its children supports this file type
     */
    fun supportMimeType(
        fileData: MDFileData,
    ): Boolean = currentSupportMimeType(fileData) || children.any { it.supportMimeType(fileData) }

    /**
     * @return true if this reader only supports this file type
     */
    fun currentSupportMimeType(
        fileData: MDFileData,
    ): Boolean = fileData.mimeType?.let {
        it in supportedMimeTypes
    } == true

    /**
     * @return true if this reader or any of its children support this file header
     */
    suspend fun validHeader(
        fileData: MDFileData,
    ): Boolean = validHeaderForCurrent(
        fileData = fileData
    ) || children.any {
        it.validHeader(fileData)
    }

    /**
     * @return true if this reader directly supports this file header
     */
    suspend fun validHeaderForCurrent(
        fileData: MDFileData,
    ): Boolean = try {
        val inputStream = openInputStream(fileData)
        if (inputStream == null) {
            Log.d("file_reader", "reader $label, null input stream for file $fileData")
        } else {
            inputStream.use { closableStream ->
                tryParseFileHeader(closableStream)
            }
            Log.d("file_reader", "reader $label, parsed header $fileData")
        }
        inputStream != null
    } catch (error: Error) {
        Log.d("file_reader", "reader $label, has an error parsing header $fileData, ${error.cause}")
        false
    } catch (exception: Exception) {
        Log.d("file_reader", "reader $label, has an exception parsing header $fileData, ${exception.cause}")
        false
    }

    /**
     * try to read this file with this reader or any of its wrappers,
     * at first it try to get first wrapper that support the file type starting from root
     * @param onReadItem passing each new dev.bayan_ibrahim.my_dictionary.core.design_system.group.item read in the file return false to stop reading
     * @param onInvalidStream callback if failed to open the input stream
     * @param onUnsupportedFile if this reader can't read this file, nor its wrappers
     * @param onReadStreamError callback if caught an error while reading  the input stream
     */
    suspend fun readFile(
        fileData: MDFileData,
        /**
         * this is required to cancel the flow
         */
        onComplete: suspend () -> Unit,
        onInvalidStream: suspend () -> Unit = {},
        onUnsupportedFile: suspend () -> Unit = {},
        onReadStreamError: suspend (throwable: Throwable) -> Unit = {},
        /**
         * try get first wrapper that support the file type
         */
        tryGetReaderByMimeType: Boolean = true,
        /**
         * try get first wrapper that support the file header
         */
        tryGetReaderByFileHeader: Boolean = true,
        onReadItem: suspend (Data) -> Boolean,
    ) {
        val validReader = if (tryGetReaderByMimeType) {
            getReaderByType(fileData)
        } else {
            null
        } ?: if (tryGetReaderByFileHeader) {
            getReaderByHeader(fileData)
        } else {
            null
        }

        if (validReader == null) {
            onUnsupportedFile()
            return
        }

        val stream = openInputStream(fileData)
        if (stream == null) {
            onInvalidStream()
            return
        }
        stream.use { closableStream ->
            try {
                validReader.parseInputStream(closableStream, onReadItem)
                onComplete()
            } catch (throwable: Throwable) {
                onReadStreamError(throwable)
                if (throwable is CloseTransactionException) {
                    throw throwable
                }
            }
        }
    }

    /**
     * try parse the stream or throw an exception, **DON'T PARSE ALL THE STREAM BECAUSE THIS METHOD MAY BE CALLED SEVERAL TIMES**
     * @param stream will be closed automatically
     */
    protected abstract suspend fun tryParseFileHeader(stream: InputStream)

    /**
     * how to parse the input stream and call the [onReadItem] every time a new dev.bayan_ibrahim.my_dictionary.core.design_system.group.item is read
     * @param stream will be closed automatically
     */
    protected abstract suspend fun parseInputStream(
        stream: InputStream,
        onReadItem: suspend (Data) -> Boolean,
    )

    /**
     * @return first reader support [fileData] by its type or null starting from current then children
     */
    private suspend fun getReaderByType(fileData: MDFileData): MDFileReaderDecorator<Data>? {
        if (currentSupportMimeType(fileData)) {
            return this
        }

        return children.firstNotNullOfOrNull {
            it.getReaderByType(fileData)
        }
    }

    /**
     * @return first reader support [fileData] by its header or null starting from current then children
     */
    private suspend fun getReaderByHeader(fileData: MDFileData): MDFileReaderDecorator<Data>? {
        if (validHeaderForCurrent(fileData)) {
            return this
        }

        return children.firstNotNullOfOrNull {
            it.getReaderByHeader(fileData)
        }
    }
}