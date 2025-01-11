package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFileSize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.flatten
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileSize
import java.io.IOException
import java.io.OutputStream

class MDAndroidFileManager(
    @ApplicationContext
    private val context: Context,
) : FileManager {
    private val contentResolver
        get() = context.contentResolver
    private val trash = mutableMapOf<String, MDDocumentData>()

    override suspend fun getDocumentData(
        uri: Uri?,
    ): Result<MDDocumentData> = runCatching {
        uri ?: throw IllegalArgumentException("can not get file data from null uri")
        val mimeType = contentResolver.getType(uri)
        val isTree = isTree(uri)
        val filePath = getFilePath(uri).getOrNull() // Attempt to retrieve the file path

        if (isTree && mimeType == null) {
            val name = getDirName(uri)
            MDDocumentData(
                uri = uri,
                name = name ?: "-",
                mimeType = MDDocumentData.DIR_MIME_TYPE,
                filePath = filePath,
            )
        } else {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val name = getFileName(cursor)
                val sizeBytes = getFileSize(cursor)

                MDDocumentData(
                    uri = uri,
                    name = name,
                    mimeType = mimeType,
                    size = sizeBytes,
                    filePath = filePath
                )
            } ?: throw IllegalArgumentException("Can not get file data, unable to open cursor for uri $uri")
        }
    }

    private fun getFileSize(cursor: Cursor): MDFileSize {
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        return cursor.getLong(sizeIndex).asFileSize()
    }

    private fun getFileName(cursor: Cursor): String {
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        return cursor.getString(nameIndex)
    }

    private fun getDirName(uri: Uri?) = DocumentsContract.getTreeDocumentId(uri)?.split("/")?.lastOrNull()

    private fun isTree(uri: Uri?) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        DocumentsContract.isTreeUri(uri)
    } else {
        // TODO, test on legacy devices
        DocumentsContract.getTreeDocumentId(uri) != null
    }

    override suspend fun createSubDocument(
        parent: Uri,
        childName: String,
        childMimeType: String?,
    ): Result<MDDocumentData> = runCatching {
        // Convert the tree URI to a document URI
        val parentDocumentUri = DocumentsContract.buildDocumentUriUsingTree(
            parent,
            DocumentsContract.getTreeDocumentId(parent)
        ) ?: throw IllegalArgumentException("Invalid document uri from document tree uri $parent")
        DocumentsContract.createDocument(
            contentResolver,
            parentDocumentUri,
            childMimeType ?: "",
            childName
        )?.let {
            getDocumentData(it)
        } ?: throw IllegalArgumentException("")
    }.flatten()

    override suspend fun getSubDocuments(
        uri: Uri,
        includeFiles: Boolean,
        includeDirs: Boolean,
    ): Result<List<MDDocumentData>> = runCatching {
        val children = mutableListOf<MDDocumentData>()
        contentResolver.query(
            DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri)),
            null,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val childUri = DocumentsContract.buildDocumentUriUsingTree(
                    uri,
                    cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID))
                )
                val child = getDocumentData(childUri).getOrThrow()
                if ((includeFiles && !child.isDir) || (includeDirs && !child.isDir)) {
                    children.add(child)
                }
            }
        }
        children
    }

    override suspend fun getFilePath(
        uri: Uri,
    ): Result<String> = runCatching {
        val docId = DocumentsContract.getDocumentId(uri)
        docId.split(":").lastOrNull() ?: throw IllegalArgumentException("Invalid URI: $uri")
    }

    override suspend fun openOutputStream(uri: Uri?): Result<OutputStream> = runCatching {
        uri ?: throw IllegalArgumentException("Can not open output stream form null uri")
        contentResolver.openOutputStream(uri) ?: throw IOException("Can not open output stream uri $uri")
    }

    override suspend fun addToTrash(
        key: String,
        document: MDDocumentData,
        deleteOldIfExisted: Boolean,
    ) {
        if (deleteOldIfExisted) {
            trash[key]?.let {
                deleteDocument(it)
            }
        }
        trash[key] = document
    }

    override fun restoreFromTrash(key: String): Boolean {
        return trash.remove(key) != null
    }

    override suspend fun deleteDocument(data: MDDocumentData): Result<Boolean> {
        return runCatching {
            DocumentsContract.deleteDocument(contentResolver, data.uri)
        }
    }

    override suspend fun clearTrash(
        filter: suspend (String, MDDocumentData) -> Boolean,
    ): Result<Int> = runCatching {
        var count = 0
        val trashSnapshot = trash.filter {
            filter(it.key, it.value)
        }
        trashSnapshot.map {
            val result = deleteDocument(it.value).getOrThrow()
            if (result) {
                count++
            }
            trash.remove(it.key)
        }
        count
    }
}