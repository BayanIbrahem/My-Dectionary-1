package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

interface MDFileLanguagePart : MDFilePart {
    val code: String
    val typeTags: List<LanguageTypeTag>

    interface LanguageTypeTag {
        val id: Long?
        val name: String
        val relations: List<MDNameWithOptionalId>
        fun toTypeTag(language: Language): WordTypeTag
    }

    fun toLanguage(): Language
    fun toTypeTags(): List<WordTypeTag> = typeTags.map { it.toTypeTag(toLanguage()) }
}
