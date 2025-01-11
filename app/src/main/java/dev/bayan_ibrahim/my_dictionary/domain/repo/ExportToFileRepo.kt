package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.data.ExportProgress
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import kotlinx.coroutines.flow.Flow

interface ExportToFileRepo {
    fun export(
        wordsIds: Set<Long>,
        parts: Set<MDFilePartType>,
        exportDirectory: MDDocumentData,
        exportFileType: MDFileType,
        exportFileName: String,
    ): Flow<ExportProgress>
}