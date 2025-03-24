package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser.poi

import kotlinx.datetime.LocalDate

sealed interface MDSheetLanguageType {
    data object Global : MDSheetLanguageType
    data class LanguageSpecific(val languageCode: String) : MDSheetLanguageType
    companion object {
        operator fun invoke(rawName: String): MDSheetLanguageType = TODO()
    }
}

enum class MDSheetDataType(val label: String) {
    TAG("tag"),
    WORD("word"),
    WORD_CLASS("wordClass"),
    GENERAL("");

    companion object {
        operator fun invoke(label: String?): MDSheetDataType {
            label ?: return GENERAL
            return entries.firstOrNull {
                it.label.equals(label, true)
            } ?: GENERAL
        }
    }

}

data class MDSheet(
    val languageType: MDSheetLanguageType,
    val dataType: MDSheetDataType,
    val name: String,
    val index: Int,
) {
    companion object {
        operator fun invoke(rawName: String, index: Int): MDSheet {
            val result = languageRegex.findAll(rawName).toList()
            val languageCode = result.firstOrNull()?.value?.lowercase()
            val languageType = if (languageCode == null) {
                MDSheetLanguageType.Global
            } else {
                MDSheetLanguageType.LanguageSpecific(languageCode)
            }
            val name = result.getOrNull(1)?.value?.lowercase()
            val dataType = MDSheetDataType(name)
            return MDSheet(languageType, dataType, rawName, index)
        }

        val languageRegex = "([a-zA-Z]{2,3})-(.+)".toRegex()
    }
}

sealed interface MDCellData {
    data class Text(val text: String) : MDCellData
    data class Numeric(val number: Double) : MDCellData
    data class Date(val date: LocalDate) : MDCellData
    data class Bool(val value: Boolean) : MDCellData
    data class Error(val error: String, val code: Int = UNKNOWN_CELL_TYPE_ERROR) : MDCellData {
        companion object {
            const val UNKNOWN_CELL_TYPE_ERROR = 0
            const val UNKNOWN_FORMULA_TYPE_ERROR = 1
            const val FORMULA_TYPE_ERROR = 2
        }
    }

    data object Blank : MDCellData

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

data class MDCell(
    override val data: MDCellData,
    val rowNumber: Int,
    override val columnIndex: Int,
) : MDRowCell(data, columnIndex)