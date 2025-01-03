package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word

interface MDFileWordPart : MDFilePart {
    val language: String
    val meaning: String
    val translation: String
    val transcription: String?
    val tags: List<MDFileTagPart>
    val examples: List<String>
    val additionalTranslations: List<String>
    val typeTag: MDNameWithOptionalId?
    val relatedWords: Map<MDNameWithOptionalId, String>

    fun toWord(): Word
}
