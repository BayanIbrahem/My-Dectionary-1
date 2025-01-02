package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part

interface MDFileTagPart : MDFilePart, MDNameWithOptionalId {
    val color: String?
    val passColorToChildren: Boolean
}
