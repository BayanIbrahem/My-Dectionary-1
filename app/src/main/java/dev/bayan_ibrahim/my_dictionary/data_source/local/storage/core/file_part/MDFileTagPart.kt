package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag

interface MDFileTagPart : MDFilePart, StrIdentifiable {
    val color: String?
    val passColorToChildren: Boolean?

    fun toTag(): Tag?
}
