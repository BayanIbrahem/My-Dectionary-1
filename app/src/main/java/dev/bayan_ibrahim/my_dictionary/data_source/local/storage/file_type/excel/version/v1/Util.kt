package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1

data class ExcelMapParseException(
    val objectName: String,
    val requiredKey: String,
    val providedMap: Map<String, String>,
) :
    IllegalArgumentException() {
    override val message: String
        get() = "Error in parsing $objectName from map, missing key `$requiredKey` current values $providedMap"
}