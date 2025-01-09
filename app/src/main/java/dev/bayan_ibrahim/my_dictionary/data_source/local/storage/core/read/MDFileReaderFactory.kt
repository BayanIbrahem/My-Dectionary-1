package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import java.io.InputStream

interface MDFileReaderFactory<Reader : MDFileReader> {
    val availableVersions: List<Int>
    val name: String
    suspend fun openInputStream(fileData: MDDocumentData): InputStream
    suspend fun getVersion(fileData: MDDocumentData): Int
    suspend fun getVersionOrNull(fileData: MDDocumentData): Int? = try {
        getVersion(fileData)
    } catch (e: Exception) {
        null
    }

    suspend fun buildReaderForVersion(version: Int): Reader

    suspend fun buildReaderForData(fileData: MDDocumentData): Reader {
        val version = getVersion(fileData)
        return buildReaderForVersion(version)
    }
}