package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType

class MDFileWriterAbstractFactory(
    private val factories: Collection<MDFileWriterFactory<out MDFileWriter<out MDFilePart>>>,
) {
    val availableTypes = factories.map { it.fileType }.toSet()

    suspend fun getFactory(
        fileType: MDFileType,
    ): MDFileWriterFactory<out MDFileWriter<out MDFilePart>> = getFactoryOrNull(
        fileType = fileType
    ) ?: throw IllegalArgumentException("Can't get writer factory for type $fileType available versions are $availableTypes")

    suspend fun getFactoryOrNull(fileType: MDFileType): MDFileWriterFactory<out MDFileWriter<out MDFilePart>>? {
        return factories.firstOrNull {
            it.fileType == fileType
        }
    }
}