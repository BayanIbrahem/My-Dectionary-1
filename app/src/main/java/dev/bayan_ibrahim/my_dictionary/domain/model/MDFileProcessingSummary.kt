package dev.bayan_ibrahim.my_dictionary.domain.model

data class MDFileProcessingSummary(
    val wordsCount: Int = 0,
    val languagesCount: Int = 0,
    val tagsCount: Int = 0,
    val wordTypeTagCount: Int = 0,
    val wordTypeTagRelationCount: Int = 0,
    val totalEntriesRead: Int = 0,
    val status: MDFileProcessingSummaryStatus = MDFileProcessingSummaryStatus.IDLE,
)

enum class MDFileProcessingSummaryStatus {
    IDLE,
    RUNNING,
    COMPLETED,
}