package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActions

interface ImportFromFileRepo {
    suspend fun checkFileIfValid(fileData: MDFileData): Boolean

    suspend fun processFile(
        fileData: MDFileData,
        outputSummaryActions: MDFileProcessingSummaryActions,
        existedWordStrategy: MDPropertyConflictStrategy,
        corruptedWordStrategy: MDPropertyCorruptionStrategy,
        allowedFileParts: Set<MDFilePartType> = MDFilePartType.entries.toSet(),
    )

    suspend fun getAvailablePartsInFile(
        fileData: MDFileData,
        outputSummaryActions: MDFileProcessingSummaryActions,
    ): List<MDFilePartType>
}