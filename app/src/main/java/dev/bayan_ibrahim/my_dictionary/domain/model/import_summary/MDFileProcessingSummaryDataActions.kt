package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

interface MDFileProcessingSummaryDataActions {
    fun recognizeRawWord(): MDFileProcessingSummaryDataActions
    fun recognizeNewWord(): MDFileProcessingSummaryDataActions
    fun recognizeCorruptedWord(): MDFileProcessingSummaryDataActions
    fun recognizeIgnoredWord(): MDFileProcessingSummaryDataActions
    fun recognizeUpdatedWord(): MDFileProcessingSummaryDataActions
    fun recognizeLanguage(): MDFileProcessingSummaryDataActions
    fun recognizeNewTypeTag(): MDFileProcessingSummaryDataActions
    fun recognizeNewTypeTagRelation(): MDFileProcessingSummaryDataActions
    fun recognizeTags(newRecognizedTags: Collection<String>): MDFileProcessingSummaryDataActions
    fun onError(error: FileProcessingSummaryErrorType): MDFileProcessingSummaryDataActions
    fun reset()
}
