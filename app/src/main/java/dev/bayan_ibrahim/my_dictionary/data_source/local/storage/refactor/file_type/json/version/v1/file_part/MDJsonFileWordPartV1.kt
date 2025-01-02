package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDNameWithOptionalId
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_part.MDJsonFileWordPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
    override val version: Int = 1

    @Transient
    override val relatedWords: Map<MDNameWithOptionalId, String> = relatedWordsList.associate {
        (it as MDNameWithOptionalId) to it.relatedWord
    }

    @Serializable
    data class TypeTag(
        @SerialName(ID_KEY)
        override val id: Long? = null,
        @SerialName(NAME_KEY)
        override val name: String,
    ) : MDNameWithOptionalId {
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
