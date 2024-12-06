package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

interface MDFileProcessingSummary {
    val status: MDFileProcessingSummaryStatus
    val recognizedLanguages: Map<Language, Boolean>
    val currentLanguage: Language?
    val currentLanguageSummary: MDFileProcessingSummaryData
    val totalSummary: MDFileProcessingSummaryData
    val processingStartTime: Long
    val processingEndTime: Long

    val runningTime: Duration?
        get() = if (processingStartTime > 0 && processingEndTime > 0) {
            (processingEndTime - processingStartTime).milliseconds
        } else if (processingStartTime > 0) {
            (System.currentTimeMillis() - processingStartTime).milliseconds
        } else {
            null

        }

    fun asString(
        separator: String = "\n",
        runningTime: Duration? = this.runningTime,
    ): String = buildString {
        appendLine("Summary - status: $status")
        appendLine("running time: ${this@MDFileProcessingSummary.runningTime ?: "-"}")
        appendLine("languages: new ${recognizedLanguages.count { it.value }}, existed: ${recognizedLanguages.count { !it.value }}")
        appendLine("current language $currentLanguage")
        appendLine("Current Language summary")
        append(currentLanguageSummary.asString(separator))
        appendLine("Total Summary")
        append(totalSummary.asString(separator))
    }
}
