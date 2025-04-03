package dev.bayan_ibrahim.my_dictionary.domain.model.excel

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