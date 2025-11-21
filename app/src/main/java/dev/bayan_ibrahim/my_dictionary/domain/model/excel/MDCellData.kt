package dev.bayan_ibrahim.my_dictionary.domain.model.excel

import kotlinx.datetime.LocalDate

sealed interface MDCellData {
    fun toStringValue(): String

    /**
     * using [toStringValue] then split value to lines
     */
    fun toStringListValue(): List<String> {
        return toStringValue().lines().filter {
            it.isNotBlank()
        }
    }

    data class Text(val text: String) : MDCellData {
        override fun toStringValue(): String = text
    }

    data class Numeric(val number: Double) : MDCellData {
        override fun toStringValue(): String = number.toString()
    }

    data class Date(val date: LocalDate) : MDCellData {
        override fun toStringValue(): String = "${date.year}-${date.monthNumber}-${date.dayOfMonth}"
    }

    data class Bool(val value: Boolean) : MDCellData {
        override fun toStringValue(): String = value.toString()
    }

    data class Error(val error: String, val code: Int = UNKNOWN_CELL_TYPE_ERROR) : MDCellData {
        override fun toStringValue(): String = ""

        companion object {
            const val UNKNOWN_CELL_TYPE_ERROR = 0
            const val UNKNOWN_FORMULA_TYPE_ERROR = 1
            const val FORMULA_TYPE_ERROR = 2
        }
    }

    data object Blank : MDCellData {
        override fun toStringValue(): String = ""
    }

    fun toRowCell(columnIndex: Int): MDRowCell = MDRowCell(
        data = this,
        columnIndex = columnIndex
    )

    fun toCell(columnIndex: Int, rowNumber: Int): MDCell = MDCell(
        data = this,
        rowNumber = columnIndex,
        columnIndex = rowNumber
    )
}