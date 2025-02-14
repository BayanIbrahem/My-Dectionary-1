package dev.bayan_ibrahim.my_dictionary.domain.model.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDFileType(
    private val typeExtensionValue: String,
    val mimeType: String,
) : LabeledEnum {
    Unknown("", "*/*"),

    //    Excel("xls", "application/excel"),
    Json("json", "application/json"),
//    Xml("xml", "application/xml"),
//    CSV("csv", "text/csv");
    ;

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Unknown -> stringResource(R.string.general)
//            Excel -> "Excel file"
            Json -> stringResource(R.string.json_file)
//            Xml -> "Xml file"
//            CSV -> "CSV file"
        }
    val typeExtensionWithDot = if (typeExtensionValue.isBlank()) "" else ".$typeExtensionValue"
    val typeExtensionLabel = "*$typeExtensionWithDot"

    companion object {
        val validEntries = entries.filterNot { it == Unknown }
    }
}
