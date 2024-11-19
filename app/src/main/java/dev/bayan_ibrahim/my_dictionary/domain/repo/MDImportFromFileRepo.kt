package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummary
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import kotlinx.coroutines.flow.Flow

interface MDImportFromFileRepo {
    suspend fun checkFileIFValid(fileData: MDFileData): Boolean

    fun processFile(
        fileData: MDFileData,
        existedWordStrategy: MDFileStrategy,
        corruptedWordStrategy: MDFileStrategy,
        onInvalidStream: () -> Unit = {},
        onUnsupportedFile: () -> Unit = {},
        onReadStreamError: (throwable: Throwable) -> Unit = {},
        /** try get first wrapper that support the file type */
        tryGetReaderByMimeType: Boolean = true,
        /** try get first wrapper that support the file header */
        tryGetReaderByFileHeader: Boolean = true,
    ): Flow<MDFileProcessingSummary>
}