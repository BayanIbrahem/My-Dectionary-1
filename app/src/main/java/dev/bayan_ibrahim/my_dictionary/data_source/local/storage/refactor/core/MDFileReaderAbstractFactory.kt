package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core

import android.util.Log
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_reader.MDFileReader
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData

class MDFileReaderAbstractFactory(
    private val factories: Collection<MDFileReaderFactory<out MDFileReader>>,
) {
    val availableVersions: List<Int>
        get() = factories.map { it.availableVersions }.flatten().distinct()
    suspend fun getFirstSuitableFactory(
        fileData: MDFileData,
    ): MDFileReaderFactory<out MDFileReader> = getFirstSuitableFactoryOrNull(
        fileData
    ) ?: throw IllegalArgumentException("can not file suitable factory can read its version for file ${fileData.name} (${fileData.uri})")

    suspend fun getFirstSuitableFactoryOrNull(fileData: MDFileData): MDFileReaderFactory<out MDFileReader>? {
        return factories.firstNotNullOfOrNull {
            try {
                it.getVersion(fileData)
                it
            } catch (e: Exception) {
                Log.d("file_picker", "factory ${it.name} isn't able to open file $e")
                null
            }
        }
    }
}