package dev.bayan_ibrahim.my_dictionary.domain.model.file

import android.net.Uri
import android.provider.DocumentsContract

data class MDDocumentData(
    val uri: Uri,
    val name: String,
    val mimeType: String? = null,
    val size: MDFileSize = MDFileSize.zero,
    val filePath: String? = null,
) {
    val isDir: Boolean
        get() = mimeType == DIR_MIME_TYPE

    companion object {
        const val DIR_MIME_TYPE = DocumentsContract.Document.MIME_TYPE_DIR
    }
}