package dev.bayan_ibrahim.my_dictionary.domain.model

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.asFileSize

data class MDFileData(
    val uri: Uri,
    val name: String,
    val mimeType: String?,
    val sizeBytes: MDFileSize = MDFileSize.zero,
)

fun Context.readFileData(uri: Uri?): MDFileData? {
    uri ?: return null

    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        val sizeBytes = cursor.getLong(sizeIndex)
        val mimeType = contentResolver.getType(uri)

        MDFileData(
            uri = uri,
            name = name,
            mimeType = mimeType,
            sizeBytes = sizeBytes.asFileSize()
        )
    }
}