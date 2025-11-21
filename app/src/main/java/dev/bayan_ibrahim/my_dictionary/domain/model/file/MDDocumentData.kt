package dev.bayan_ibrahim.my_dictionary.domain.model.file

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import java.io.IOException
import java.io.InputStream

data class MDDocumentData(
    val uri: Uri,
    val name: String,
    val mimeType: String? = null,
    val size: MDFileSize = MDFileSize.zero,
    val filePath: String? = null,
) {
    /**
     * @return true if the uri is not [Uri.EMPTY]
     */
    val valid: Boolean
        get() = uri != Uri.EMPTY

    val isDir: Boolean
        get() = mimeType == DIR_MIME_TYPE

    val sfx: String?
        get() = name.nullIfInvalid()?.let { name: String ->
            val lastDotIndex = name.lastIndexOf('.')
            if (lastDotIndex != -1 && lastDotIndex < name.length - 1) {
                name.substring(lastDotIndex + 1)
            } else {
                null
            }
        }

    companion object {
        const val DIR_MIME_TYPE = DocumentsContract.Document.MIME_TYPE_DIR
        val Blank = MDDocumentData(Uri.EMPTY, "", "")
    }

    context(Context)
    fun open(): InputStream {
        // TODO, custom exception
        return contentResolver.openInputStream(
            uri
        ) ?: throw IOException("can not open input stream for file ${name}, (${uri})")
    }
}

