package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.collections.immutable.PersistentSet

class MDFileProcessingMutableSummary : MDFileProcessingSummary {
    override var prevStep: MDFileProcessingSummaryActionsStep? by mutableStateOf(null)
    override var currentStep: MDFileProcessingSummaryActionsStep by mutableStateOf(MDFileProcessingSummaryActionsStep.Start)
    override val expectedSteps: SnapshotStateList<MDFileProcessingSummaryActionsStep> = mutableStateListOf()
    override var isParsingAvailablePartsOnly: Boolean by mutableStateOf(true)
    override var availableParts: PersistentSet<MDFilePartType>? by mutableStateOf(null)
    override val exceptions: SnapshotStateMap<MDFileProcessingSummaryStepException, Int> = mutableStateMapOf()
    override val warnings: SnapshotStateMap<MDFileProcessingSummaryStepWarning, Int> = mutableStateMapOf()

    override var status: MDFileProcessingSummaryStatus by mutableStateOf(MDFileProcessingSummaryStatus.IDLE)
    override val recognizedLanguages: SnapshotStateMap<LanguageCode, Boolean> = mutableStateMapOf()
    override val recognizedTypeTags: SnapshotStateMap<Pair<LanguageCode, String>, Boolean> = mutableStateMapOf()
    override val recognizedTypeTagsRelations: SnapshotStateMap<Triple<LanguageCode, String, String>, Boolean> = mutableStateMapOf()
    override val recognizedContextTags: SnapshotStateMap<String, Boolean> = mutableStateMapOf()
    override val recognizedWords: SnapshotStateMap<Triple<LanguageCode, String, String>, Boolean> = mutableStateMapOf()
    override val recognizedCorruptedWords: SnapshotStateMap<Triple<LanguageCode, String, String>, Boolean> = mutableStateMapOf()

    override var processingStartTime: Long by mutableLongStateOf(-1)
    override var processingEndTime: Long by mutableLongStateOf(-1)

    fun reset() {
        prevStep = null
        currentStep = MDFileProcessingSummaryActionsStep.Start
        expectedSteps.clear()
        isParsingAvailablePartsOnly = true
        availableParts = null
        exceptions.clear()
        warnings.clear()

        status = MDFileProcessingSummaryStatus.IDLE
        recognizedLanguages.clear()
        recognizedTypeTags.clear()
        recognizedTypeTagsRelations.clear()
        recognizedContextTags.clear()
        recognizedWords.clear()
        recognizedCorruptedWords.clear()

        processingStartTime = -1
        processingEndTime - 1
    }
}
