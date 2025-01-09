package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType

typealias Writer = MDFileWriter<MDFilePart>

class MDFileWriterFactory(
    private val writers: Collection<out Writer>,
) {
    suspend fun getWriterForTypeOrNull(type: MDFileType): Writer? = writers.firstOrNull { it.type == type }

    suspend fun getWriterForType(
        type: MDFileType,
    ): Writer = getWriterForTypeOrNull(type) ?: throw IllegalArgumentException(
        "Can not build file writer, Invalid Type $type, available types are ${writers.map { it.type }.toSet()}"
    )
}