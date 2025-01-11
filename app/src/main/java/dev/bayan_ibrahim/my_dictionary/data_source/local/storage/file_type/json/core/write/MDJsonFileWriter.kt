package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.write

import com.google.protobuf.any
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write.MDFilePartWriter
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write.MDFileWriter
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.JSON_VERSION_KEY
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.OutputStream

class MDJsonFileWriter(
    private val json: Json,
    override val version: Int,
    private val openStream: (data: MDDocumentData) -> OutputStream,
) : MDFileWriter<MDJsonFilePart> {
    override val type: MDFileType = MDFileType.Json
    override suspend fun openOutputStream(data: MDDocumentData): OutputStream {
        return openStream(data)
    }

    override suspend fun getFilePartWriter(
        type: MDFilePartType,
        getData: suspend () -> List<MDFilePart>,
    ): MDFilePartWriter<MDJsonFilePart> {
        return MDJsonFilePartWriter(
            version = version,
            type = type,
            json = json,
            getData = getData
        )
    }

    override suspend fun writeFile(
        stream: OutputStream,
        parts: Set<MDFilePartType>,
        onProgress: suspend (index: Int, total: Int, part: MDFilePartType) -> Unit,
        onRequestPart: suspend (part: MDFilePartType) -> List<MDFilePart>,
    ): Boolean {
        require(parts.isNotEmpty()) {
            "Can not write empty parts list into a stream, pass at least one file part type"
        }
        return stream.bufferedWriter().use { writer ->
            writer.write("{")

            writer.writeJsonKey(JSON_VERSION_KEY)
            writer.write(":")
            writer.write(version.toString())
            writer.write(",")

            val indexOfLast = parts.count().dec()
            var anyDecoded = false
            parts.sorted().forEachIndexed { i, type ->
                val partWriter = getFilePartWriter(type) {
                    onRequestPart(type)
                }

                val result = partWriter.writePart(
                    writer = writer,
                    onProgress = { i, total ->
                        onProgress(i, total, type)
                    }
                )

                anyDecoded = anyDecoded || result

                if (i < indexOfLast) {
                    writer.write(",")
                }
            }

            writer.write("}")
            anyDecoded
        }
    }

    private fun BufferedWriter.writeJsonKey(key: String, quote: String = "\"") {
        write(quote)
        write(key)
        write(quote)
    }
}