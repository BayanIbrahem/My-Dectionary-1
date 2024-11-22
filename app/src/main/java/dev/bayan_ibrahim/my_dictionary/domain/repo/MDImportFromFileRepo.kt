package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingMutableSummaryActions

interface MDImportFromFileRepo {
    suspend fun checkFileIfValid(fileData: MDFileData): Boolean

    suspend fun processFile(
        fileData: MDFileData,
        outputSummaryActions: MDFileProcessingMutableSummaryActions,
        existedWordStrategy: MDFileStrategy = MDFileStrategy.OverrideValid,
        corruptedWordStrategy: MDFileStrategy = MDFileStrategy.Ignore,
        tryGetReaderByMimeType: Boolean = true,
        tryGetReaderByFileHeader: Boolean = true,
        allowedLanguages: Set<Language> = allLanguages.values.toSet(),
    )
}