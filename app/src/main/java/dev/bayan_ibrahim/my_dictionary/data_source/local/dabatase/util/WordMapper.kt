package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordWithContextTagsAndRelatedWordsRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import kotlinx.datetime.Clock

@JvmName("WordWithRelatedWordsAsWordModel")
fun WordWithContextTagsAndRelatedWordsRelation.asWordModel(
    tag: WordClass? = null,
): Word = Word(
    id = this.word.id!!,
    meaning = this.word.meaning.meaningViewNormalize,
    translation = this.word.translation.meaningViewNormalize,
    additionalTranslations = this.word.additionalTranslations,
    language = this.word.languageCode.code.getLanguage(),
    tags = this.tags.map { it.asModel() }.toSet(),
    transcription = this.word.transcription,
    examples = this.word.examples,
    wordClass = tag,
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
    wordClass = null,
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
    result[WordLexicalRelationType.Antonym] = this.antonym.map { WordLexicalRelation.Antonym(it) }
    result[WordLexicalRelationType.Hyponym] = this.hyponym.map { WordLexicalRelation.Hyponym(it) }
    result[WordLexicalRelationType.Hypernym] = this.hypernym.map { WordLexicalRelation.Hypernym(it) }
    result[WordLexicalRelationType.Meronym] = this.meronym.map { WordLexicalRelation.Meronym(it) }
    result[WordLexicalRelationType.Holonym] = this.holonym.map { WordLexicalRelation.Holonym(it) }
    result[WordLexicalRelationType.Homonym] = this.homonym.map { WordLexicalRelation.Polysemy(it) }
    result[WordLexicalRelationType.Polysemy] = this.polysemy.map { WordLexicalRelation.Polysemy(it) }
    result[WordLexicalRelationType.Prototype] = this.prototype.map { WordLexicalRelation.Prototype(it) }
    result[WordLexicalRelationType.Metonymy] = this.meronym.map { WordLexicalRelation.Meronym(it) }
    result[WordLexicalRelationType.Collocation] = this.collocation.map { WordLexicalRelation.Collocation(it) }
    result[WordLexicalRelationType.Homograph] = this.homograph.map { WordLexicalRelation.Homograph(it) }
    result[WordLexicalRelationType.Homophone] = this.homophone.map { WordLexicalRelation.Homophone(it) }

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
    wordClassId = this.wordClass?.id?.nullIfInvalid(),
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

fun Word.asRelatedWords(): List<WordClassRelatedWordEntity> = this.relatedWords.mapNotNull { word ->
    word.relationId.nullIfInvalid()?.let {
        WordClassRelatedWordEntity(
            id = word.id.nullIfInvalid(),
            relationId = word.relationId,
            baseWordId = this.id,
            word = word.value,
        )
    }
}

