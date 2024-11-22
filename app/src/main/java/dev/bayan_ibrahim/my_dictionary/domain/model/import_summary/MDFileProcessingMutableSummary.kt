package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.bayan_ibrahim.my_dictionary.domain.model.Language

class MDFileProcessingMutableSummary : MDFileProcessingSummary {
    override var status: MDFileProcessingSummaryStatus by mutableStateOf(MDFileProcessingSummaryStatus.IDLE)
    override val recognizedLanguages: SnapshotStateMap<Language, Boolean> = mutableStateMapOf()
    override var currentLanguage: Language? by mutableStateOf(null)
    override val currentLanguageSummary: MDFileProcessingMutableSummaryData = MDFileProcessingMutableSummaryData()
    override val totalSummary: MDFileProcessingMutableSummaryData = MDFileProcessingMutableSummaryData()
    override var processingStartTime: Long by mutableLongStateOf(-1)
    override var processingEndTime: Long by mutableLongStateOf(-1)

    fun reset() {
        status = MDFileProcessingSummaryStatus.IDLE
        recognizedLanguages.clear()
        currentLanguage = null
        currentLanguageSummary.reset()
        totalSummary.reset()
        processingStartTime = -1
        processingEndTime = -1
    }

}
