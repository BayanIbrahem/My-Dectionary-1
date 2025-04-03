package dev.bayan_ibrahim.my_dictionary.domain.model.excel

open class MDRowCell(
    open val data: MDCellData,
    open val columnIndex: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MDRowCell) return false

        if (data != other.data) return false
        if (columnIndex != other.columnIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + columnIndex
        return result
    }

    override fun toString(): String {
        return "MDRowCell(data=$data, columnIndex=$columnIndex)"
    }

    fun toCell(rowNumber: Int): MDCell = MDCell(
        data = data,
        rowNumber = columnIndex,
        columnIndex = rowNumber
    )
}