package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

enum class MDFileProcessingSummaryStatus(val isRunning: Boolean) {
    IDLE(false),
    Running(true),
    Completed(false),
}
