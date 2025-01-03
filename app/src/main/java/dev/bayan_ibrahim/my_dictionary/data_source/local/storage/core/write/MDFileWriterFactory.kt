package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType

interface MDFileWriterFactory<Writer : MDFileWriter<out MDFilePart>> {
    val availableVersions: List<Int>
    val name: String
    val fileType: MDFileType
    suspend fun buildReaderForVersionOrNull(version: Int): Writer?
    suspend fun buildReaderForVersion(version: Int): Writer = buildReaderForVersionOrNull(version) ?: throw IllegalArgumentException(
        "Can not build file writer, Invalid Version $version, available versions are $availableVersions"
    )
}