package dev.bayan_ibrahim.my_dictionary.domain.model.file

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

    override val strLabel: String
        get() = when (this) {
            Unknown -> "General"
//            Excel -> "Excel file"
            Json -> "Json file"
//            Xml -> "Xml file"
//            CSV -> "CSV file"
        }
    val typeExtensionWithDot = if (typeExtensionValue.isBlank()) "" else ".$typeExtensionValue"
    val typeExtensionLabel = "*$typeExtensionWithDot"

    companion object {
        val validEntries = entries.filterNot { it == Unknown }
    }
}
