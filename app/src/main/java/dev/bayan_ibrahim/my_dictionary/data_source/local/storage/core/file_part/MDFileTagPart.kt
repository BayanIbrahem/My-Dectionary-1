package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag

interface MDFileTagPart : MDFilePart, MDNameWithOptionalId {
    val color: String?
    val passColorToChildren: Boolean

    fun toContextTag(): ContextTag
}
