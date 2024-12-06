package dev.bayan_ibrahim.my_dictionary.domain.model.word

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

data class Word(
    val id: Long,
    val meaning: String,
    val translation: String,
    val additionalTranslations: List<String>,
    val language: Language,
    val tags: List<String> = emptyList(),
    val transcription: String = INVALID_TEXT,
    val examples: List<String> = emptyList(),
    val wordTypeTag: WordTypeTag? = null,
    val relatedWords: List<RelatedWord> = emptyList(),
    val learningProgress: Float = 0f,
)
