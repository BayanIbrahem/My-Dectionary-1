package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write.MDFilePartWriter
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.JsonStringifier
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.getJsonPartKeyOfVersion
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import kotlinx.serialization.json.Json
import java.io.Writer

class MDJsonFilePartWriter(
    override val version: Int,
    override val type: MDFilePartType,
    private val json: Json,
    private val getData: suspend () -> List<MDFilePart>,
) : MDFilePartWriter<MDJsonFilePart> {
    private suspend fun writeDataToStream(
        data: List<MDFilePart>,
        writer: Writer,
        onProgress: suspend (Int, Int) -> Unit = { _, _ -> },
    ): Boolean {
        if (data.isEmpty()) return false
        val key = getPartKey()
        writer.write("\"$key\":[")
        val count = data.count()
        val lastIndex = count.dec()
        data.forEachIndexed { i, item ->
            val stringifiedItem = item.stringify(JsonStringifier(json))
            writer.write(stringifiedItem)
            if (i < lastIndex) {
                writer.write(",")
            }
            onProgress(i, count)
        }
        writer.write("]")
        return true
    }

    override suspend fun writePart(
        writer: Writer,
        onProgress: suspend (Int, Int) -> Unit,
    ): Boolean {
        return writeDataToStream(
            data = getData(),
            writer = writer,
            onProgress = onProgress
        )
    }

    private fun getPartKey(): String {
        return getJsonPartKeyOfVersion(type, version)
    }
}