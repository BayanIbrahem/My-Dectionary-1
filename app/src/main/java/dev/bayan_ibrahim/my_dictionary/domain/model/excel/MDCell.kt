package dev.bayan_ibrahim.my_dictionary.domain.model.excel

data class MDCell(
    override val data: MDCellData,
    val rowNumber: Int,
    override val columnIndex: Int,
) : MDRowCell(data, columnIndex)