package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

class MDFileProcessingMutableSummaryActionsImpl(
    private val summary: MDFileProcessingMutableSummary,
) : MDFileProcessingMutableSummaryActions {
    override val currentLanguageSummaryActions = MDFileProcessingSummaryDataActionsImpl(summary.currentLanguageSummary)
    override val totalSummaryActions = MDFileProcessingSummaryDataActionsImpl(summary.totalSummary)

    override fun onRecognizeLanguage(language: Language, isNew: Boolean) {
        // count of languages is some how little so no need for set or hashset
        if (language !in summary.recognizedLanguages) {
            summary.recognizedLanguages[language] = isNew
        }
    }

    override fun onSetCurrentLanguage(language: Language) {
        val differentLanguage = summary.currentLanguage != language
        summary.currentLanguage = language
        if (differentLanguage) {
            summary.currentLanguageSummary.reset()
        }
    }


    override fun onSplitFile() {
        summary.status = MDFileProcessingSummaryStatus.SPLIT_FILE
        // this is the start of processing the file so we store the total time
        summary.processingStartTime = System.currentTimeMillis()
    }

    override fun onScanDatabase() {
        summary.status = MDFileProcessingSummaryStatus.SCAN_DATABASE
    }

    override fun onStoreData() {
        summary.status = MDFileProcessingSummaryStatus.STORE_DATA
    }

    override fun onComplete() {
        summary.status = MDFileProcessingSummaryStatus.COMPLETED
        summary.processingEndTime = System.currentTimeMillis()
    }

    override fun recognizeRawWord(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeRawWord()
        totalSummaryActions.recognizeRawWord()
    }

    override fun recognizeNewWord(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeNewWord()
        totalSummaryActions.recognizeNewWord()
    }

    override fun recognizeCorruptedWord(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeCorruptedWord()
        totalSummaryActions.recognizeCorruptedWord()
    }

    override fun recognizeIgnoredWord(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeIgnoredWord()
        totalSummaryActions.recognizeIgnoredWord()
    }

    override fun recognizeUpdatedWord(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeUpdatedWord()
        totalSummaryActions.recognizeUpdatedWord()
    }

    override fun recognizeLanguage(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeLanguage()
        totalSummaryActions.recognizeLanguage()
    }

    override fun recognizeNewTypeTag(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeNewTypeTag()
        totalSummaryActions.recognizeNewTypeTag()
    }

    override fun recognizeNewTypeTagRelation(): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeNewTypeTagRelation()
        totalSummaryActions.recognizeNewTypeTagRelation()
    }

    override fun recognizeTags(newRecognizedTags: Collection<String>): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.recognizeTags(newRecognizedTags)
        totalSummaryActions.recognizeTags(newRecognizedTags)
    }

    override fun onError(error: FileProcessingSummaryErrorType): MDFileProcessingSummaryDataActions = apply {
        currentLanguageSummaryActions.onError(error)
        totalSummaryActions.onError(error)
    }

    override fun reset() {
        summary.reset()
    }
}