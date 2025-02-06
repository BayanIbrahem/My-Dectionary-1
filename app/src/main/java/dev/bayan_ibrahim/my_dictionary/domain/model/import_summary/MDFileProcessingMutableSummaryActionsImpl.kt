package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.End
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.GetAvailableParts
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.ParseAndSaveContextTags
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.ParseAndSaveWordsClasses
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.ParseAndSaveWords
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.ParseSaveLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.RecognizingFileReader
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.RecognizingFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.import_summary.MDFileProcessingSummaryActionsStep.Start
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode

class MDFileProcessingSummaryActionsImpl(
    private val summary: MDFileProcessingMutableSummary,
) : MDFileProcessingSummaryActions {
    override fun onStep(step: MDFileProcessingSummaryActionsStep) {
        summary.prevStep = summary.currentStep
        summary.currentStep = step
        summary.expectedSteps.setAll(
            expectedStepsOf(
                step = step,
                parseForAvailablePartsOnly = summary.isParsingAvailablePartsOnly,
                availableParts = summary.availableParts
            )
        )
        if (step == Start) {
            summary.status = MDFileProcessingSummaryStatus.Running
        } else if (step == End) {
            summary.status = MDFileProcessingSummaryStatus.Completed
        }
    }

    override fun onException(stepException: MDFileProcessingSummaryStepException) {
        val prevCount = summary.exceptions[stepException] ?: 0
        summary.exceptions[stepException] = prevCount.inc()
        onStep(End)
    }

    override fun onWarning(stepWarning: MDFileProcessingSummaryStepWarning) {
        val prevCount = summary.warnings[stepWarning] ?: 0
        summary.warnings[stepWarning] = prevCount.inc()
    }

    override fun recognizeLanguage(code: LanguageCode, new: Boolean) {
        summary.recognizedLanguages.putIfAbsent(code, new)
    }

    override fun recognizeWordClass(languageCode: LanguageCode, name: String, new: Boolean) {
        summary.recognizedWordsClasses.putIfAbsent(Pair(languageCode, name), new)
    }

    override fun recognizeWordClassRelation(languageCode: LanguageCode, wordClassName: String, relationLabel: String, new: Boolean) {
        summary.recognizedWordsClassesRelations.putIfAbsent(Triple(languageCode, wordClassName, relationLabel), new)
    }

    override fun recognizeContextTag(value: String, new: Boolean) {
        summary.recognizedContextTags.putIfAbsent(value, new)
    }

    override fun recognizeWord(languageCode: LanguageCode, meaning: String, translation: String, new: Boolean) {
        summary.recognizedWords.putIfAbsent(Triple(languageCode, meaning, translation), new)
    }

    override fun recognizeCorruptedWord(languageCode: LanguageCode, meaning: String, translation: String, new: Boolean) {
        summary.recognizedCorruptedWords.putIfAbsent(Triple(languageCode, meaning, translation), new)
    }

    override fun reset() {
        summary.reset()
    }

    private fun expectedStepsOf(
        step: MDFileProcessingSummaryActionsStep,
        parseForAvailablePartsOnly: Boolean,
        availableParts: Set<MDFilePartType>?,
    ): List<MDFileProcessingSummaryActionsStep> = when (step) {
        Start -> listOf(RecognizingFileType)
        RecognizingFileType -> listOf(RecognizingFileReader)
        RecognizingFileReader -> listOf(GetAvailableParts)
        GetAvailableParts -> if (parseForAvailablePartsOnly) listOf(End) else {
            listOf(
                ParseSaveLanguages,
                ParseAndSaveWordsClasses,
                ParseAndSaveContextTags,
                ParseAndSaveWords,
            ).filterForAvailableFileParts(availableParts)
        }

        ParseSaveLanguages ->
            listOf(
                ParseAndSaveWordsClasses,
                ParseAndSaveContextTags,
                ParseAndSaveWords,
            ).filterForAvailableFileParts(availableParts)

        ParseAndSaveWordsClasses ->
            listOf(
                ParseAndSaveContextTags,
                ParseAndSaveWords,
            ).filterForAvailableFileParts(availableParts)

        ParseAndSaveContextTags ->
            listOf(
                ParseAndSaveWords,
            ).filterForAvailableFileParts(availableParts)

        ParseAndSaveWords -> listOf(End)
        End -> listOf()
    }

    private fun List<MDFileProcessingSummaryActionsStep>.filterForAvailableFileParts(
        availableParts: Set<MDFilePartType>?,
    ): List<MDFileProcessingSummaryActionsStep> {
        if (availableParts.isNullOrEmpty()) return this
        return filter {
            when (it) {
                ParseSaveLanguages, ParseAndSaveWordsClasses -> MDFilePartType.Language in availableParts
                ParseAndSaveContextTags -> MDFilePartType.Tag in availableParts
                ParseAndSaveWords -> MDFilePartType.Word in availableParts
                else -> false
            }
        }.ifEmpty {
            listOf(End)
        }
    }
}