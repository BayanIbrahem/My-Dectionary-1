package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MDFileProcessingMutableSummaryData : MDFileProcessingSummaryData {
    override var totalParsedWords: Int by mutableIntStateOf(0)
    override var newWordsCount: Int by mutableIntStateOf(0)
    override var updatedWordsCount: Int by mutableIntStateOf(0)
    override var ignoredWordsCount: Int by mutableIntStateOf(0)
    override var corruptedWordsCount: Int by mutableIntStateOf(0)
    override var newLanguagesCount: Int by mutableIntStateOf(0)
    override var newTagsCount: Int by mutableIntStateOf(0)
    override var newWordTypeTagCount: Int by mutableIntStateOf(0)
    override var newWordTypeTagRelationsCount: Int by mutableIntStateOf(0)
    override var error: FileProcessingSummaryErrorType? by mutableStateOf(null)
    override fun reset() {
        totalParsedWords = 0
        newWordsCount = 0
        updatedWordsCount = 0
        ignoredWordsCount = 0
        corruptedWordsCount = 0
        newLanguagesCount = 0
        newTagsCount = 0
        newWordTypeTagCount = 0
        newWordTypeTagRelationsCount = 0
        error = null
    }
}
