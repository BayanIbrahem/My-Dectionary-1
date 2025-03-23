package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

import kotlinx.datetime.LocalDate

data class SheetCell(
    val key: SheetCellKey,
    val value: Value,
) {
    sealed class Value {
        /**
         * if the cell is an actual value
         */
        data class Text(val text: String) : Value() {
            override fun toString(): String = text
        }

        /**
         * if the cell content is a string reference
         */
        data class Ref(val ref: Int) : Value() {
            override fun toString(): String = "@$ref"
        }

        data class Bool(val value: Boolean) : Value() {
            override fun toString(): String = "b:$value"
        }

        /**
         * this is either a date or a number according to its [styleIndex]
         * you can convert it to [Date] by [toDate] or to [Text] by [toText]
         */
        data class RawNumber(val value: Int, val styleIndex: Int) : Value() {
            override fun toString(): String = "$value"

            fun toDate() = Date(value)
            fun toText() = Text(value.toString())
            fun isDate(formatCode: Int) = isDateStyleCode(formatCode)
            fun isDate(formatCodeList: List<Int>) = formatCodeList.getOrNull(styleIndex)?.let(::isDateStyleCode) == true
            fun toDateOrText(formatCode: Int) = if (isDate(formatCode)) toDate() else toText()
            fun toDateOrText(formatCodeList: List<Int>) = if (isDate(formatCodeList)) toDate() else toText()

            companion object Companion {
                fun isDateStyleCode(code: Int): Boolean = code in sequenceOf(
                    FORMAT_CODE_DATE_SHORT,
                    FORMAT_CODE_DATE_MEDIUM,
                    FORMAT_CODE_DATE_DAY_MONTH,
                    FORMAT_CODE_DATE_MONTH_YEAR,
                    FORMAT_CODE_DATETIME,
                )

                /**
                 * Render cell content without any specific formatting
                 */
                const val FORMAT_CODE_GENERAL = 0

                /**
                 * Integer value format
                 */
                const val FORMAT_CODE_INTEGER = 1

                /**
                 * Floating point number with two decimal places
                 */
                const val FORMAT_CODE_TWO_DECIMALS = 2

                /**
                 * Number format with thousand separator
                 */
                const val FORMAT_CODE_THOUSANDS = 3

                /**
                 * Number format with thousand separator and two decimal places
                 */
                const val FORMAT_CODE_THOUSANDS_TWO_DECIMALS = 4

                /**
                 * Percentage format without decimal places
                 */
                const val FORMAT_CODE_PERCENT = 9

                /**
                 * Percentage format with two decimal places
                 */
                const val FORMAT_CODE_PERCENT_TWO_DECIMALS = 10

                /**
                 * Scientific notation with one decimal place
                 */
                const val FORMAT_CODE_SCIENTIFIC = 11

                /**
                 * Fraction format (e.g., 1/2)
                 */
                const val FORMAT_CODE_FRACTION_ONE_DIGIT = 12

                /**
                 * Fraction format with two-digit denominator (e.g., 1/20)
                 */
                const val FORMAT_CODE_FRACTION_TWO_DIGITS = 13

                /**
                 * Date format (MM-DD-YY)
                 */
                const val FORMAT_CODE_DATE_SHORT = 14

                /**
                 * Date format (D-MMM-YY)
                 */
                const val FORMAT_CODE_DATE_MEDIUM = 15

                /**
                 * Date format (D-MMM)
                 */
                const val FORMAT_CODE_DATE_DAY_MONTH = 16

                /**
                 * Date format (MMM-YY)
                 */
                const val FORMAT_CODE_DATE_MONTH_YEAR = 17

                /**
                 * Time format (H:MM AM/PM)
                 */
                const val FORMAT_CODE_TIME_12H = 18

                /**
                 * Time format (H:MM:SS AM/PM)
                 */
                const val FORMAT_CODE_TIME_12H_SECONDS = 19

                /**
                 * Time format (H:MM)
                 */
                const val FORMAT_CODE_TIME_24H = 20

                /**
                 * Time format (H:MM:SS)
                 */
                const val FORMAT_CODE_TIME_24H_SECONDS = 21

                /**
                 * Date and time format (M/D/YY H:MM)
                 */
                const val FORMAT_CODE_DATETIME = 22

                /**
                 * Number format with parentheses for negative values
                 */
                const val FORMAT_CODE_ACCOUNTING = 37

                /**
                 * Number format with parentheses for negative values in red
                 */
                const val FORMAT_CODE_ACCOUNTING_RED = 38

                /**
                 * Number format with parentheses and two decimal places
                 */
                const val FORMAT_CODE_ACCOUNTING_TWO_DECIMALS = 39

                /**
                 * Number format with parentheses and two decimal places in red
                 */
                const val FORMAT_CODE_ACCOUNTING_TWO_DECIMALS_RED = 40

                /**
                 * Time format (MM:SS)
                 */
                const val FORMAT_CODE_TIME_MINUTES_SECONDS = 45

                /**
                 * Time format (H:MM:SS, including hours above 24)
                 */
                const val FORMAT_CODE_TIME_ELAPSED = 46

                /**
                 * Time format (MMSS.0)
                 */
                const val FORMAT_CODE_TIME_COMPACT = 47

                /**
                 * Scientific notation with one significant figure
                 */
                const val FORMAT_CODE_SCIENTIFIC_EXPONENT = 48

                /**
                 * Text format (interprets input as text, even if it's a number)
                 */
                const val FORMAT_CODE_TEXT = 49
            }
        }

        data class Date(val value: Int) : Value() {
            override fun toString(): String = "$date"
            val epochDays: Int get() = value - EPOCH_DAYS_OFFSET
            val date: LocalDate
                get() = LocalDate.fromEpochDays(epochDays)

            companion object {
                /**
                 * offset in the sheet is from 1/1/1900 so the offset of epoch is this value
                 */
                private const val EPOCH_DAYS_OFFSET = 25569
            }
        }

        data class Error(val function: String?, val value: String?) : Value() {
            override fun toString(): String = "err:#f:$function | #v:$value"
        }

        data object Empty : Value() {
            override fun toString(): String = "#N/A"
        }
    }

    override fun toString(): String = "$key=$value"
}
