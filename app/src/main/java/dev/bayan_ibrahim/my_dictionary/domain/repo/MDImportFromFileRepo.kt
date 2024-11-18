package dev.bayan_ibrahim.my_dictionary.domain.repo

import android.net.Uri
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileProcessingSummary
import kotlinx.coroutines.flow.Flow

interface MDImportFromFileRepo {
    suspend fun checkFileIFValid(fileData: MDFileData): Boolean

    fun processFile(
        fileData: MDFileData,
        onInvalidStream: () -> Unit = {},
        onUnsupportedFile: () -> Unit = {},
        onReadStreamError: (throwable: Throwable) -> Unit = {},
        /** try get first wrapper that support the file type */
        tryGetReaderByMimeType: Boolean = true,
        /** try get first wrapper that support the file header */
        tryGetReaderByFileHeader: Boolean = true,
    ): Flow<MDFileProcessingSummary>
}