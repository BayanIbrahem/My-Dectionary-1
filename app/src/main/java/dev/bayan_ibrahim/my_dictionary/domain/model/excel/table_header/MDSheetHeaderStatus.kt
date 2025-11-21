package dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header

enum class MDSheetHeaderStatus {
    /**
     * existed but ignored
     */
    Ignored,
    /**
     * existed and passing
     */
    Assigned,

    /**
     * not existed and required
     */
    Required,
}