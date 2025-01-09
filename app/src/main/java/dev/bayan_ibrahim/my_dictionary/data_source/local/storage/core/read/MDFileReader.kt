package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import java.io.InputStream

interface MDFileReader {
    val version: Int

    suspend fun openInputStream(data: MDDocumentData): InputStream
    suspend fun getReaderOfPart(part: MDFilePartType): MDFilePartReader<out MDFilePart>
    suspend fun getReaderOfParts(
        parts: Collection<MDFilePartType>,
    ): Map<MDFilePartType, MDFilePartReader<out MDFilePart>> = parts.associateWith {
        getReaderOfPart(it)
    }

    suspend fun getAvailablePartsOfFile(data: MDDocumentData): List<MDFilePartType>

    fun supportVersion(version: Int): Boolean = version <= this.version
}