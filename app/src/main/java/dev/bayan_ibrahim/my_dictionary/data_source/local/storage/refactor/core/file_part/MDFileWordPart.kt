package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part

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
}
