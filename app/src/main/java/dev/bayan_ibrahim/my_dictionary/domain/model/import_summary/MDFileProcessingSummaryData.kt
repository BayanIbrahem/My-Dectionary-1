package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

interface MDFileProcessingSummaryData {
    val totalParsedWords: Int

    // word
    val newWordsCount: Int
    val updatedWordsCount: Int
    val ignoredWordsCount: Int
    val corruptedWordsCount: Int

    // language
    val newLanguagesCount: Int

    // tags
    val newTagsCount: Int

    // word type tag
    val newWordTypeTagCount: Int

    // word type tag relation
    val newWordTypeTagRelationsCount: Int

    val error: FileProcessingSummaryErrorType?
    fun asString(separator: String = "\n"): String = buildString {
        append("totalParsedWords=")
        appendLine(totalParsedWords)
        append(", newWordsCount=")
        appendLine(newWordsCount)
        append(", updatedWordsCount=")
        appendLine(updatedWordsCount)
        append(", ignoredWordsCount=")
        appendLine(ignoredWordsCount)
        append(", corruptedWordsCount=")
        appendLine(corruptedWordsCount)
        append(", newLanguagesCount=")
        appendLine(newLanguagesCount)
        append(", newTagsCount=")
        appendLine(newTagsCount)
        append(", newWordTypeTagCount=")
        appendLine(newWordTypeTagCount)
        append(", newWordTypeTagRelationsCount=")
        appendLine(newWordTypeTagRelationsCount)
        append(", error=")
        appendLine(error)
    }

    fun reset()
}
