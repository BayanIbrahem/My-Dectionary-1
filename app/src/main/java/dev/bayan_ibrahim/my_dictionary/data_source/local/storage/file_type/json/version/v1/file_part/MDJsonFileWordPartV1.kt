package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileWordPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.StrIdentifiable
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileWordPart
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord as ModelRelatedWord

@Serializable
data class MDJsonFileWordPartV1(
    @SerialName(LANGUAGE_KEY)
    override val language: String,
    @SerialName(MEANING_KEY)
    override val meaning: String,
    @SerialName(TRANSLATION_KEY)
    override val translation: String,
    @SerialName(TRANSCRIPTION_KEY)
    override val transcription: String? = null,
    @SerialName(CONTEXT_TAGS_KEY)
    override val contextTags: List<MDJsonFileTagPartV1> = emptyList(),
    @SerialName(EXAMPLES_KEY)
    override val examples: List<String> = emptyList(),
    @SerialName(ADDITIONAL_TRANSLATION_KEY)
    override val additionalTranslations: List<String> = emptyList(),
    @SerialName(TYPE_TAG_KEY)
    override val typeTag: TypeTag? = null,
    @SerialName(RELATED_WORDS_KEY)
    private val relatedWordsList: List<RelatedWord> = emptyList(),
    @SerialName(LEXICAL_RELATED_WORDS_KEY)
    override val lexicalRelatedWords: List<LexicalRelation> = emptyList(),
) : MDJsonFileWordPart {

    override suspend fun jsonStringify(json: Json): String {
        return json.encodeToString(serializer(), this)
    }

    @Transient
    override val relatedWords: Map<StrIdentifiable, String> = relatedWordsList.associate {
        (it as StrIdentifiable) to it.relatedWord
    }

    override fun toWord(): Word {
        return Word(
            id = INVALID_ID,
            meaning = meaning,
            translation = translation,
            additionalTranslations = additionalTranslations,
            language = language.code.getLanguage(),
            tags = contextTags.mapNotNull { it.toContextTag() }.toSet(),
            transcription = transcription ?: "",
            examples = examples,
            wordTypeTag = typeTag?.toModelTypeTag(language)?.copy(
                relations = relatedWordsList.map {
                    WordTypeTagRelation(
                        label = it.name,
                        id = INVALID_ID,
                        wordsCount = 0,
                    )
                }
            ),
            relatedWords = relatedWordsList.map {
                ModelRelatedWord(
                    id = INVALID_ID,
                    baseWordId = INVALID_ID,
                    relationId = INVALID_ID,
                    relationLabel = it.name,
                    value = it.relatedWord,
                )
            },
            // no related words
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            lexicalRelations = this.lexicalRelatedWords.groupBy {
                it.type
            }.mapValues { (_, values) ->
                values.map { relation ->
                    WordLexicalRelation.invoke(
                        type = relation.type,
                        relatedWord = relation.value
                    )
                }
            }
        )
    }

    @Serializable
    data class TypeTag(
        @SerialName(NAME_KEY)
        override val name: String,
    ) : StrIdentifiable {
        fun toModelTypeTag(language: String): WordTypeTag {
            return WordTypeTag(
                id = INVALID_ID,
                name = name,
                language = language.code.getLanguage(),
                relations = emptyList(),
                wordsCount = 0,
            )
        }

        companion object Companion {
            const val NAME_KEY = "typeTagName"
        }
    }

    @Serializable
    data class RelatedWord(
        @SerialName(RELATION_LABEL_KEY)
        override val name: String,
        @SerialName(RELATED_WORD_KEY)
        val relatedWord: String,
    ) : StrIdentifiable {

        companion object Companion {
            const val RELATION_LABEL_KEY = "relation"
            const val RELATED_WORD_KEY = "word"
        }
    }

    @Serializable
    data class LexicalRelation(
        @SerialName(TYPE_KEY)
        override val type: WordLexicalRelationType,
        @SerialName(VALUE_KEY)
        override val value: String,
    ) : MDFileWordPart.LexicalRelation {
        companion object {
            const val TYPE_KEY = "lexicalRelationType"
            const val VALUE_KEY = "lexicalRelationValue"
        }

    }

    companion object Companion {
        const val LANGUAGE_KEY = "language"
        const val MEANING_KEY = "meaning"
        const val TRANSLATION_KEY = "translation"
        const val TRANSCRIPTION_KEY = "transcription"
        const val CONTEXT_TAGS_KEY = "contextTags"
        const val EXAMPLES_KEY = "examples"
        const val ADDITIONAL_TRANSLATION_KEY = "additionalTranslations"
        const val TYPE_TAG_KEY = "typeTag"
        const val RELATED_WORDS_KEY = "relatedWords"
        const val LEXICAL_RELATED_WORDS_KEY = "lexicalRelatedWords"
    }
}
