package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDNameWithOptionalId
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileWordPart
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
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
    @SerialName(TAGS_KEY)
    override val tags: List<MDJsonFileTagPartV1> = emptyList(),
    @SerialName(EXAMPLES_KEY)
    override val examples: List<String> = emptyList(),
    @SerialName(ADDITIONAL_TRANSLATION_KEY)
    override val additionalTranslations: List<String> = emptyList(),
    @SerialName(TYPE_TAG_KEY)
    override val typeTag: TypeTag? = null,
    @SerialName(RELATED_WORDS_KEY)
    private val relatedWordsList: List<RelatedWord>,
) : MDJsonFileWordPart {

    override suspend fun jsonStringify(json: Json): String {
        return json.encodeToString(serializer(), this)
    }

    @Transient
    override val relatedWords: Map<MDNameWithOptionalId, String> = relatedWordsList.associate {
        (it as MDNameWithOptionalId) to it.relatedWord
    }

    override fun toWord(): Word {
        return Word(
            id = INVALID_ID,
            meaning = meaning,
            translation = translation,
            additionalTranslations = additionalTranslations,
            language = language.code.getLanguage(),
            tags = tags.map { it.toContextTag() }.toSet(),
            transcription = transcription ?: "",
            examples = examples,
            wordTypeTag = typeTag?.toModelTypeTag(language)?.copy(
                relations = relatedWordsList.map {
                    WordTypeTagRelation(
                        label = it.name,
                        id = it.id ?: INVALID_ID,
                        wordsCount = 0,
                    )
                }
            ),
            relatedWords = relatedWordsList.map {
                ModelRelatedWord(
                    id = INVALID_ID,
                    baseWordId = INVALID_ID,
                    relationId = it.id ?: INVALID_ID,
                    relationLabel = it.name,
                    value = it.relatedWord,
                )
            },
            // no related words
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        )
    }

    @Serializable
    data class TypeTag(
        @SerialName(ID_KEY)
        override val id: Long? = null,
        @SerialName(NAME_KEY)
        override val name: String,
    ) : MDNameWithOptionalId {
        fun toModelTypeTag(language: String): WordTypeTag {
            return WordTypeTag(
                id = id ?: INVALID_ID,
                name = name,
                language = language.code.getLanguage(),
                relations = emptyList(),
                wordsCount = 0,
            )
        }

        companion object Companion {
            const val ID_KEY = "id"// TODO, check serial name
            const val NAME_KEY = "typeTagName"// TODO, check serial name
        }
    }

    @Serializable
    data class RelatedWord(
        @SerialName(ID_KEY)
        override val id: Long? = null,
        @SerialName(RELATION_LABEL_KEY)
        override val name: String = "",
        @SerialName(RELATED_WORD_KEY)
        val relatedWord: String,
    ) : MDNameWithOptionalId {

        companion object Companion {
            const val ID_KEY = "id"// TODO, check serial name
            const val RELATION_LABEL_KEY = "relation"// TODO, check serial name
            const val RELATED_WORD_KEY = "word"// TODO, check serial name
        }
    }

    companion object Companion {
        const val LANGUAGE_KEY = "language"// TODO, check serial name
        const val MEANING_KEY = "meaning"// TODO, check serial name
        const val TRANSLATION_KEY = "translation"// TODO, check serial name
        const val TRANSCRIPTION_KEY = "transcription"// TODO, check serial name
        const val TAGS_KEY = "tags"// TODO, check serial name
        const val EXAMPLES_KEY = "examples"// TODO, check serial name
        const val ADDITIONAL_TRANSLATION_KEY = "additionalTranslations" // TODO, check serial name
        const val TYPE_TAG_KEY = "typeTag"// TODO, check serial name
        const val RELATED_WORDS_KEY = "relatedWords"// TODO, check serial name
    }
}
