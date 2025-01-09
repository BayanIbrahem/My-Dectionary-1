package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager

import android.net.Uri
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import java.io.OutputStream

interface FileManager {
    suspend fun getDocumentData(uri: Uri?): Result<MDDocumentData>

    suspend fun createSubDocument(
        parent: Uri,
        childName: String,
        childMimeType: String? = null,
    ): Result<MDDocumentData>


    suspend fun createSubDir(
        parent: Uri,
        dirName: String,
    ): Result<MDDocumentData> = createSubDocument(
        parent = parent,
        childName = dirName,
        childMimeType = MDDocumentData.DIR_MIME_TYPE
    )

    suspend fun createSubDocument(
        parent: MDDocumentData,
        childName: String,
        childMimeType: String? = null,
    ): Result<MDDocumentData> = if (parent.isDir) {
        createSubDocument(
            parent.uri,
            childName,
            childMimeType,
        )
    } else {
        Result.failure(
            // TODO, create a specific exception
            IllegalArgumentException(
                "can not create sub document from $parent (parent not a dir)"
            )
        )
    }

    suspend fun createSubDir(
        parent: MDDocumentData,
        childName: String,
    ): Result<MDDocumentData> = createSubDocument(
        parent = parent,
        childName = childName,
        childMimeType = MDDocumentData.DIR_MIME_TYPE
    )

    suspend fun getSubDocuments(uri: Uri, includeFiles: Boolean = true, includeDirs: Boolean = true): Result<List<MDDocumentData>>
    suspend fun getFilePath(uri: Uri): Result<String>

    suspend fun getSubDocuments(
        fileData: MDDocumentData,
        includeFiles: Boolean = true,
        includeDirs: Boolean = true,
    ): Result<List<MDDocumentData>> = getSubDocuments(
        uri = fileData.uri,
        includeFiles = includeFiles,
        includeDirs = includeDirs
    )

    suspend fun getFilePath(
        fileData: MDDocumentData,
    ): Result<String> = getFilePath(
        uri = fileData.uri
    )

    suspend fun openOutputStream(
        uri: Uri?,
    ): Result<OutputStream>

    suspend fun openOutputStream(
        fileData: MDDocumentData?,
    ): Result<OutputStream> = openOutputStream(fileData?.uri)
}