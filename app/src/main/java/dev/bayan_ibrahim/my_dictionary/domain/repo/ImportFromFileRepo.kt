package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActions
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag

interface ImportFromFileRepo {
    suspend fun checkFileIfValid(fileData: MDDocumentData): Boolean

    suspend fun processFile(
        fileData: MDDocumentData,
        outputSummaryActions: MDFileProcessingSummaryActions,
        existedWordStrategy: MDPropertyConflictStrategy,
        corruptedWordStrategy: MDPropertyCorruptionStrategy,
        extraTags: List<ContextTag> = emptyList(),
        extraTagsStrategy: MDExtraTagsStrategy = MDExtraTagsStrategy.All,
        allowedFileParts: Set<MDFilePartType> = MDFilePartType.entries.toSet(),
    )

    suspend fun getAvailablePartsInFile(
        fileData: MDDocumentData,
        outputSummaryActions: MDFileProcessingSummaryActions,
    ): List<MDFilePartType>
}