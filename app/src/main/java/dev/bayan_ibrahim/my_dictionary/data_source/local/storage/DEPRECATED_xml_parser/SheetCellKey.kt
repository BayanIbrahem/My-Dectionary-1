package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.DEPRECATED_xml_parser
@JvmInline
value class SheetCellKey(val key: String) {
    val column: String
        get() {
            return charsRegex.find(key).let {
                it!!.groups.last()!!.value
            }
        }
    val row: Int
        get() {
            return numbersRegex.find(key).let {
                it!!.groups.last()!!.value.toInt()
            }
        }

    override fun toString(): String {
        return "$column$row"
    }
    companion object {
        private val charsRegex = "^([A-Z]+).*".toRegex()
        private val numbersRegex = ".*(\\d+)$".toRegex()
    }
}
