package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithTagsRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word

/**
 * this method return null for [Word.wordTypeTag] and [Word.relatedWords]
 */
fun WordWithTagsRelation.asWordModel(): Word = Word(
    id = this.word.id!!,
    meaning = this.word.meaning,
    translation = this.word.translation,
    additionalTranslations = this.word.additionalTranslations,
    language = this.word.languageCode.code.language,
    tags = this.tags.asModelSet(),
    transcription = this.word.transcription,
    examples = this.word.examples,
    wordTypeTag = null,
    relatedWords = emptyList(),
    memoryDecayFactor = this.word.memoryDecayFactor,
    lastTrainTime = this.word.lastTrainTime?.asEpochMillisecondsInstant(),
    createdAt = this.word.createdAt.asEpochMillisecondsInstant(),
    updatedAt = this.word.updatedAt.asEpochMillisecondsInstant()
)