package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithContextTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import kotlinx.datetime.Clock

@JvmName("WordWithRelatedWordsAsWordModel")
fun WordWithContextTagsAndRelatedWordsRelation.asWordModel(
    tag: WordTypeTag? = null,
): Word = Word(
    id = this.word.id!!,
    meaning = this.word.meaning.meaningViewNormalize,
    translation = this.word.translation.meaningViewNormalize,
    additionalTranslations = this.word.additionalTranslations,
    language = this.word.languageCode.code.getLanguage(),
    tags = this.tags.map { it.asModel() }.toSet(),
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
    lastTrainTime = this.word.lastTrainTime?.asEpochMillisecondsInstant(),
    updatedAt = this.word.updatedAt.asEpochMillisecondsInstant(),
    lexicalRelations = word.getLexicalRelations()
)

@Deprecated("Non Consistent data, tags data is lost")
@JvmName("WordEntityAsWordModel")
fun WordEntity.asWordModel(): Word = Word(
    id = this.id ?: INVALID_ID,
    meaning = this.meaning,
    translation = this.translation,
    additionalTranslations = this.additionalTranslations,
    language = this.languageCode.code.getLanguage(),
    transcription = this.transcription,
    examples = this.examples,
    wordTypeTag = null,
    relatedWords = emptyList(),
    memoryDecayFactor = this.memoryDecayFactor,
    lastTrainTime = this.lastTrainTime?.asEpochMillisecondsInstant(),
    createdAt = this.createdAt.asEpochMillisecondsInstant(),
    updatedAt = this.updatedAt.asEpochMillisecondsInstant(),
    lexicalRelations = getLexicalRelations()
)

fun WordEntity.getLexicalRelations(): Map<WordLexicalRelationType, List<WordLexicalRelation>> {
    val result = mutableMapOf<WordLexicalRelationType, List<WordLexicalRelation>>()

    result[WordLexicalRelationType.Synonym] = this.synonym.map { WordLexicalRelation.Synonym(it) }
    result[WordLexicalRelationType.Antonym] = this.synonym.map { WordLexicalRelation.Antonym(it) }
    result[WordLexicalRelationType.Hyponym] = this.synonym.map { WordLexicalRelation.Hyponym(it) }
    result[WordLexicalRelationType.Hypernym] = this.synonym.map { WordLexicalRelation.Hypernym(it) }
    result[WordLexicalRelationType.Meronym] = this.synonym.map { WordLexicalRelation.Meronym(it) }
    result[WordLexicalRelationType.Holonym] = this.synonym.map { WordLexicalRelation.Holonym(it) }
    result[WordLexicalRelationType.Homonym] = this.synonym.map { WordLexicalRelation.Polysemy(it) }
    result[WordLexicalRelationType.Polysemy] = this.synonym.map { WordLexicalRelation.Polysemy(it) }
    result[WordLexicalRelationType.Prototype] = this.synonym.map { WordLexicalRelation.Prototype(it) }
    result[WordLexicalRelationType.Metonymy] = this.synonym.map { WordLexicalRelation.Meronym(it) }
    result[WordLexicalRelationType.Collocation] = this.synonym.map { WordLexicalRelation.Collocation(it) }
    result[WordLexicalRelationType.Homograph] = this.synonym.map { WordLexicalRelation.Homograph(it) }
    result[WordLexicalRelationType.Homophone] = this.synonym.map { WordLexicalRelation.Homophone(it) }

    return result
}

fun Word.asWordEntity(
    setUpdateTimeToNow: Boolean = true,
): WordEntity = WordEntity(
    id = this.id.nullIfInvalid(),
    meaning = this.meaning,
    normalizedMeaning = this.meaning.meaningSearchNormalize,
    translation = this.translation,
    normalizedTranslation = this.translation.meaningSearchNormalize,
    additionalTranslations = this.additionalTranslations,
    languageCode = this.language.code,
    transcription = this.transcription,
    examples = this.examples,
    wordTypeTagId = this.wordTypeTag?.id?.nullIfInvalid(),
    memoryDecayFactor = this.memoryDecayFactor,
    lastTrainTime = this.lastTrainTime?.toEpochMilliseconds(),
    createdAt = this.createdAt.toEpochMilliseconds(),
    updatedAt = (if (setUpdateTimeToNow) Clock.System.now() else this.updatedAt).toEpochMilliseconds(),
    // lexical relations
    synonym = lexicalRelations[WordLexicalRelationType.Synonym]?.map { it.relatedWord } ?: emptyList(),
    antonym = lexicalRelations[WordLexicalRelationType.Antonym]?.map { it.relatedWord } ?: emptyList(),
    hyponym = lexicalRelations[WordLexicalRelationType.Hyponym]?.map { it.relatedWord } ?: emptyList(),
    hypernym = lexicalRelations[WordLexicalRelationType.Hypernym]?.map { it.relatedWord } ?: emptyList(),
    meronym = lexicalRelations[WordLexicalRelationType.Meronym]?.map { it.relatedWord } ?: emptyList(),
    holonym = lexicalRelations[WordLexicalRelationType.Holonym]?.map { it.relatedWord } ?: emptyList(),
    homonym = lexicalRelations[WordLexicalRelationType.Homonym]?.map { it.relatedWord } ?: emptyList(),
    polysemy = lexicalRelations[WordLexicalRelationType.Polysemy]?.map { it.relatedWord } ?: emptyList(),
    prototype = lexicalRelations[WordLexicalRelationType.Prototype]?.map { it.relatedWord } ?: emptyList(),
    metonymy = lexicalRelations[WordLexicalRelationType.Metonymy]?.map { it.relatedWord } ?: emptyList(),
    collocation = lexicalRelations[WordLexicalRelationType.Collocation]?.map { it.relatedWord } ?: emptyList(),
    homograph = lexicalRelations[WordLexicalRelationType.Homograph]?.map { it.relatedWord } ?: emptyList(),
    homophone = lexicalRelations[WordLexicalRelationType.Homophone]?.map { it.relatedWord } ?: emptyList(),
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

