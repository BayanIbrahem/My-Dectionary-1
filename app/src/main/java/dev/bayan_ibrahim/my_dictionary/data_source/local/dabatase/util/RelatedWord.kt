package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

data class RelatedWord(
    val id: Long,
    val baseWordId: Long,
    val relationLabel: String,
    val value: String,
)
