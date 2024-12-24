package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part

interface MDFileLanguagePart : MDFilePart {
    val code: String
    val typeTags: List<LanguageTypeTag>

    interface LanguageTypeTag {
        val id: Long?
        val name: String
        val relations: List<MDNameWithOptionalId>
    }
}
