package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.collections.immutable.PersistentSet
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

interface MDFileProcessingSummary {
    var prevStep: MDFileProcessingSummaryActionsStep?
    var currentStep: MDFileProcessingSummaryActionsStep
    val expectedSteps: SnapshotStateList<MDFileProcessingSummaryActionsStep>
    var isParsingAvailablePartsOnly: Boolean
    var availableParts: PersistentSet<MDFilePartType>?
    val exceptions: Map<MDFileProcessingSummaryStepException, Int>
    val warnings: Map<MDFileProcessingSummaryStepWarning, Int>
    var status: MDFileProcessingSummaryStatus
    val recognizedLanguages: Map<LanguageCode, Boolean>
    val recognizedTypeTags: Map<Pair<LanguageCode, String>, Boolean>
    val recognizedTypeTagsRelations: Map<Triple<LanguageCode, String, String>, Boolean>
    val recognizedContextTags: Map<String, Boolean>
    val recognizedWords: Map<Triple<LanguageCode, String, String>, Boolean>
    val recognizedCorruptedWords: Map<Triple<LanguageCode, String, String>, Boolean>
    var processingStartTime: Long
    var processingEndTime: Long

    val runningDuration: Duration?
        get() = if (processingStartTime > 0 && processingEndTime > 0) {
            (processingEndTime - processingStartTime).milliseconds
        } else null
}
