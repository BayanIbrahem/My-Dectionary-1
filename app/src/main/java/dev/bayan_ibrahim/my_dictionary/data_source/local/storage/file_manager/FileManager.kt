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

    suspend fun deleteDocument(
        data: MDDocumentData,
    ): Result<Boolean>

    /**
     * @param key key of this file, used later if it we need to delete or restore file
     * add a file to trash that it could be deleted later,
     * used if we want to delete files in uncompleted process after facing a problem
     */
    suspend fun addToTrash(
        key: String,
        document: MDDocumentData,
        deleteOldIfExisted: Boolean = true,
    )

    /**
     * restore a file from trash if existed, call this method before [clearTrash] to mark file
     */
    fun restoreFromTrash(key: String): Boolean

    /**
     * remove all files in trash according to [filter]
     * @see [clearTrash]
     */
    suspend fun clearTrash(filter: suspend (String, MDDocumentData) -> Boolean = { _, _ -> true }): Result<Int>

    /**
     * remove files in trash according to [keys]
     * @see [clearTrash]
     */
    suspend fun clearTrash(keys: Collection<String>) = clearTrash { key, _ ->
        key in keys
    }

}