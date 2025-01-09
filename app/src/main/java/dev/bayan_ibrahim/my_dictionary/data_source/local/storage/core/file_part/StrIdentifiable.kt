package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

interface StrIdentifiable {
    val name: String

    data class Default(
        override val name: String,
    ) : StrIdentifiable
}