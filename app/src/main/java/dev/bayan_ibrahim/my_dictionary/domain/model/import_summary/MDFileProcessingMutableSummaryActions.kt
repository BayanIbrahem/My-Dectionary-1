package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import dev.bayan_ibrahim.my_dictionary.domain.model.Language

interface MDFileProcessingMutableSummaryActions : MDFileProcessingSummaryDataActions {
    val currentLanguageSummaryActions: MDFileProcessingSummaryDataActions
    val totalSummaryActions: MDFileProcessingSummaryDataActions
    fun onRecognizeLanguage(language: Language, isNew: Boolean)
    fun onSetCurrentLanguage(language: Language)
    fun onSplitFile()
    fun onScanDatabase()
    fun onStoreData()
    fun onComplete()
}
