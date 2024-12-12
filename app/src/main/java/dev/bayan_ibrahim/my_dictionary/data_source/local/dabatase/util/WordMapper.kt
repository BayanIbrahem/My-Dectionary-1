package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryDbNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.datetime.Clock

@JvmName("WordWithRelatedWordsAsWordModel")
fun WordWithRelatedWords.asWordModel(
    tag: WordTypeTag? = null,
): Word = Word(
    id = this.word.id!!,
    meaning = this.word.meaning.meaningViewNormalize,
    translation = this.word.translation.meaningViewNormalize,
    additionalTranslations = this.word.additionalTranslations,
    language = this.word.languageCode.code.language,
    tags = this.word.tags.toSet(),
    transcription = this.word.transcription,
    examples = this.word.examples,
    wordTypeTag = tag,
    relatedWords = tag?.let {
        relatedWords.map { word ->
            RelatedWord(
                id = word.related.id!!,
                baseWordId = this.word.id,
                relationId = word.related.relationId,
                relationLabel = word.relationLabel,
                value = word.related.word,
            )
        }
    } ?: emptyList(),
    createdAt = this.word.createdAt.asEpochMillisecondsInstant(),
    updatedAt = this.word.updatedAt.asEpochMillisecondsInstant()
)

@JvmName("WordEntityAsWordModel")
fun WordEntity.asWordModel(): Word = Word(
    id = this.id ?: INVALID_ID,
    meaning = this.meaning,
    translation = this.translation,
    additionalTranslations = this.additionalTranslations,
    language = this.languageCode.code.language,
    tags = this.tags.toSet(),
    transcription = this.transcription,
    examples = this.examples,
    wordTypeTag = null,
    relatedWords = emptyList(),
    learningProgress = this.learningProgress,
    createdAt = this.createdAt.asEpochMillisecondsInstant(),
    updatedAt = this.updatedAt.asEpochMillisecondsInstant()
)

fun Word.asWordEntity(
    setUpdateTimeToNow: Boolean = true,
): WordEntity = WordEntity(
    id = this.id.nullIfInvalid(),
    meaning = this.meaning,
    normalizedMeaning = this.meaning.searchQueryDbNormalize,
    translation = this.translation,
    normalizedTranslation = this.translation.searchQueryDbNormalize,
    additionalTranslations = this.additionalTranslations,
    languageCode = this.language.code.code,
    tags = this.tags.toList(),
    transcription = this.transcription,
    examples = this.examples,
    wordTypeTagId = this.wordTypeTag?.id?.nullIfInvalid(),
    learningProgress = this.learningProgress,
    createdAt = this.createdAt.toEpochMilliseconds(),
    updatedAt = (if (setUpdateTimeToNow) Clock.System.now() else this.updatedAt).toEpochMilliseconds()
)

fun Word.asRelatedWords(): List<WordTypeTagRelatedWordEntity> = this.relatedWords.mapNotNull { word ->
    word.relationId.nullIfInvalid()?.let {
        WordTypeTagRelatedWordEntity(
            id = word.id.nullIfInvalid(),
            relationId = word.relationId,
            baseWordId = this.id,
            word = word.value,
        )
    }
}

fun LanguageWordSpaceEntity.asWordSpaceModel(): LanguageWordSpace = LanguageWordSpace(
    language = languageCode.code.language,
    wordsCount = wordsCount,
    averageLearningProgress = averageLearningProgress,
)

