package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

data class SheetStyles(
    val cellStyles: List<SheetCellStyle>
) {
    operator fun get(i: Int): SheetCellStyle = cellStyles[i]
    fun getOrNull(i: Int): SheetCellStyle? = cellStyles.getOrNull(i)
}