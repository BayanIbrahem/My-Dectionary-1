package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import java.io.InputStream

interface MDFileReaderFactory<Reader : MDFileReader> {
    val availableVersions: List<Int>
    val name: String
    suspend fun openInputStream(fileData: MDFileData): InputStream
    suspend fun getVersion(fileData: MDFileData): Int
    suspend fun getVersionOrNull(fileData: MDFileData): Int? = try {
        getVersion(fileData)
    } catch (e: Exception) {
        null
    }

    suspend fun buildReaderForVersion(version: Int): Reader

    suspend fun buildReaderForData(fileData: MDFileData): Reader {
        val version = getVersion(fileData)
        return buildReaderForVersion(version)
    }
}