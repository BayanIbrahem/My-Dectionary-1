package dev.bayan_ibrahim.my_dictionary.domain.model.word

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import kotlinx.datetime.Instant

data class Word(
    val id: Long,
    val meaning: String,
    val translation: String,
    val additionalTranslations: List<String>,
    val language: Language,
    val tags: Set<String> = emptySet(),
    val transcription: String = INVALID_TEXT,
    val examples: List<String> = emptyList(),
    val wordTypeTag: WordTypeTag? = null,
    val relatedWords: List<RelatedWord> = emptyList(),
    val memoryDecayFactor: Float = 1f,
    val lastTrainTime: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val memorizingProbability: Float
        get() = MDTrainDataSource.Default.memoryDecayFormula(this)

    fun getMemorizingProbabilityOfTime(instant: Instant): Float {
        val probability = MDTrainDataSource.Default.memoryDecayFormula(this, instant)
        return probability
    }
}
