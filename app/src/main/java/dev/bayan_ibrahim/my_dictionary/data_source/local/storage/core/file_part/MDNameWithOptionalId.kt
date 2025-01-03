package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

interface MDNameWithOptionalId {
    val id: Long?
    val name: String

    data class Default(
        override val id: Long?,
        override val name: String,
    ) : MDNameWithOptionalId
}