package dev.bayan_ibrahim.my_dictionary.domain.model.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class MDFileType(
    private val typeExtensionValue: String,
    val mimeType: String,
) {
    Unknown("", "*/*"),
    Excel("xls", "application/excel"),
    Json("json", "application/json"),
    Xml("xml", "application/xml"),
    CSV("csv", "text/csv");

    val typeName: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Unknown -> "General"
            Excel -> "Excel file"
            Json -> "Json file"
            Xml -> "Xml file"
            CSV -> "CSV file"
        }
    val typeExtension = if (typeExtensionValue.isBlank()) "*" else "*.$typeExtensionValue"

    companion object {
        val entriesMimeType: Array<String> = MDFileType.entries.mapNotNull {
            if (it == Unknown) null else it.mimeType
        }.toTypedArray()
    }
}
