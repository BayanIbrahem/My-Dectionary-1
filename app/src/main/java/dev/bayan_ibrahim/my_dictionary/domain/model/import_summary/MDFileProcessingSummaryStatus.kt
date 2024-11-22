package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

enum class MDFileProcessingSummaryStatus(val isRunning: Boolean) {
    IDLE(false),
    SPLIT_FILE(true),
    SCAN_DATABASE(true),
    STORE_DATA(true),
    COMPLETED(false),
}
