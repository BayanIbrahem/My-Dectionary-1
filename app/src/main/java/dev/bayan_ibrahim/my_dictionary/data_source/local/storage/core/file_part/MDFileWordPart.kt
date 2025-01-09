package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType

interface MDFileWordPart : MDFilePart {
    val language: String
    val meaning: String
    val translation: String
    val transcription: String?
    val contextTags: List<MDFileTagPart>
    val examples: List<String>
    val additionalTranslations: List<String>
    val typeTag: StrIdentifiable?
    val relatedWords: Map<StrIdentifiable, String>
    val lexicalRelatedWords: List<LexicalRelation>

    fun toWord(): Word

    interface LexicalRelation{
        val type: WordLexicalRelationType
        val value: String
    }
}

