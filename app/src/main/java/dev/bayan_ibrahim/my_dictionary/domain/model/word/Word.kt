package dev.bayan_ibrahim.my_dictionary.domain.model.word

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_INSTANT
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.datetime.Instant

data class Word(
    val id: Long,
    val meaning: String,
    val translation: String,
    val language: Language,
    val additionalTranslations: List<String> = emptyList(),
    val tags: Set<ContextTag> = emptySet(),
    val transcription: String = INVALID_TEXT,
    val examples: List<String> = emptyList(),
    val wordTypeTag: WordTypeTag? = null,
    val relatedWords: List<RelatedWord> = emptyList(),
    val memoryDecayFactor: Float = 1f,
    val lastTrainTime: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lexicalRelations: Map<WordLexicalRelationType, List<WordLexicalRelation>> = emptyMap(),
) {
    val memorizingProbability: Float
        get() = MDTrainDataSource.Default.memoryDecayFormula(this)

    fun getMemorizingProbabilityOfTime(instant: Instant): Float {
        val probability = MDTrainDataSource.Default.memoryDecayFormula(this, instant)
        return probability
    }
    companion object
}

fun Word.Companion.invalid() = Word(
    id = INVALID_ID,
    meaning = INVALID_TEXT,
    translation = INVALID_TEXT,
    language = "en".code.getLanguage(),
    createdAt = INVALID_INSTANT,
    updatedAt = INVALID_INSTANT,
)