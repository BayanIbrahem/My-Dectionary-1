package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

class MDFileProcessingSummaryDataActionsImpl(
    private val summary: MDFileProcessingMutableSummaryData = MDFileProcessingMutableSummaryData(),
) : MDFileProcessingSummaryDataActions {
    private val recognizedTags = mutableSetOf<String>()


    private fun applySummary(body: MDFileProcessingMutableSummaryData.() -> Unit): MDFileProcessingSummaryDataActions = apply {
        summary.body()
    }

    override fun recognizeRawWord() = applySummary {
        totalParsedWords += 1
    }

    override fun recognizeNewWord(): MDFileProcessingSummaryDataActions = applySummary {
        newWordsCount += 1
    }

    override fun recognizeCorruptedWord(): MDFileProcessingSummaryDataActions = applySummary {
        corruptedWordsCount += corruptedWordsCount
    }

    override fun recognizeIgnoredWord(): MDFileProcessingSummaryDataActions = applySummary {
        ignoredWordsCount += ignoredWordsCount
    }

    override fun recognizeUpdatedWord(): MDFileProcessingSummaryDataActions = applySummary {
        updatedWordsCount += updatedWordsCount
    }

    override fun recognizeLanguage(): MDFileProcessingSummaryDataActions = applySummary {
        newLanguagesCount += newLanguagesCount
    }

    override fun recognizeNewTypeTag() = applySummary {
        newWordTypeTagCount += newWordTypeTagCount
    }

    override fun recognizeNewTypeTagRelation() = applySummary {
        newWordTypeTagRelationsCount += newWordTypeTagRelationsCount
    }

    override fun recognizeTags(newRecognizedTags: Collection<String>) = applySummary {
        recognizedTags.addAll(newRecognizedTags)
        newTagsCount = recognizedTags.count()
    }

    override fun onError(error: FileProcessingSummaryErrorType) = applySummary {
        this.error = error

    }

    override fun reset() {
        summary.reset()
    }
}
