package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import java.io.OutputStream

interface MDFileWriter<out Part : MDFilePart> {
    val version: Int
    val type: MDFileType

    suspend fun getFilePartWriter(
        type: MDFilePartType,
        // Type parameter Part is declared as 'out' but occurs in 'in' position in type suspend () -> List<Part>
        getData: suspend () -> List<MDFilePart>,
    ): MDFilePartWriter<Part>

    suspend fun openOutputStream(data: MDDocumentData): OutputStream
    suspend fun writeFile(
        data: MDDocumentData,
        parts: Set<MDFilePartType>,
        onRequestPart: suspend (part: MDFilePartType) -> List<MDFilePart>,
    ) = writeFile(
        stream = openOutputStream(data),
        parts = parts,
        onRequestPart = onRequestPart
    )

    suspend fun writeFile(
        stream: OutputStream,
        parts: Set<MDFilePartType>,
        onRequestPart: suspend (part: MDFilePartType) -> List<MDFilePart>,
    )
}