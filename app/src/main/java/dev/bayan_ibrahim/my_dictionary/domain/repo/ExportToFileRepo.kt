package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType

interface ExportToFileRepo {
    suspend fun export(
        wordsIds: Set<Long>,
        parts: Set<MDFilePartType>,
        exportDirectory: MDDocumentData,
        exportFileType: MDFileType,
        exportFileName: String,
    ): Result<*>
}