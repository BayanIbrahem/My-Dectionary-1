package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages

fun WordWithRelatedWords.asWordModel(
    tag: WordTypeTag? = null,
): Word = Word(
    id = this.word.id!!,
    meaning = this.word.meaning,
    translation = this.word.translation,
    additionalTranslations = this.word.additionalTranslations,
    language = allLanguages[this.word.languageCode]!!,
    tags = this.word.tags,
    transcription = this.word.transcription,
    examples = this.word.examples,
    wordTypeTag = tag,
    relatedWords = tag?.let {
        relatedWords.map { word ->
            RelatedWord(
                id = word.id!!,
                baseWordId = this.word.id,
                relationLabel = word.relationLabel,
                value = word.word,
            )
        }
    } ?: emptyList()
)

fun Word.asWordEntity(): WordEntity = WordEntity(
    id = this.id.nullIfInvalid(),
    meaning = this.meaning,
    translation = this.translation,
    additionalTranslations = this.additionalTranslations,
    languageCode = this.language.code,
    tags = this.tags,
    transcription = this.transcription,
    examples = this.examples,
    wordTypeTagId = this.wordTypeTag?.id,
    learningProgress = this.learningProgress,
)

fun Word.asRelatedWords(): List<WordTypeTagRelatedWordEntity> = this.relatedWords.map { word ->
    WordTypeTagRelatedWordEntity(
        id = word.id.nullIfInvalid(),
        relationLabel = word.relationLabel,
        baseWordId = this.id,
        word = word.value,
    )
}

fun LanguageWordSpaceEntity.asWordSpaceModel(): LanguageWordSpace = LanguageWordSpace(
    language = allLanguages[languageCode]!!,
    wordsCount = wordsCount,
    averageLearningProgress = averageLearningProgress,
)
