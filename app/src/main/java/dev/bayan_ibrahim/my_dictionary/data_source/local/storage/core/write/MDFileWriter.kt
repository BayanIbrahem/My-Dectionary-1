package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import java.io.OutputStream

interface MDFileWriter<Part : MDFilePart> {
    val version: Int

    suspend fun getFilePartWriter(
        type: MDFilePartType,
        getData: suspend () -> List<Part>,
    ): MDFilePartWriter<Part>
    suspend fun openOutputStream(data: MDFileData): OutputStream
    suspend fun writeFile(
        data: MDFileData,
        parts: Set<MDFilePartType>,
        onRequestPart: suspend (part: MDFilePartType) -> List<Part>,
    ) = writeFile(
        stream = openOutputStream(data),
        parts = parts,
        onRequestPart = onRequestPart
    )

    suspend fun writeFile(
        stream: OutputStream,
        parts: Set<MDFilePartType>,
        onRequestPart: suspend (part: MDFilePartType) -> List<Part>,
    )
}