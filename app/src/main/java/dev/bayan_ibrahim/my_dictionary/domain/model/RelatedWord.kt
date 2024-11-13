package dev.bayan_ibrahim.my_dictionary.domain.model

data class RelatedWord(
    val id: Long,
    val baseWordId: Long,
    val relationLabel: String,
    val value: String,
)
