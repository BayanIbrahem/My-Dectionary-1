package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDNameWithOptionalId
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MDJsonFileLanguagePartV1(
    @SerialName(CODE_KEY)
    override val code: String,
    @SerialName(TYPE_TAGS_KEY)
    override val typeTags: List<TypeTag>,
) : MDJsonFileLanguagePart {
    override fun toLanguage(): Language = code.code.getLanguage()

    override suspend fun jsonStringify(json: Json): String {
        return json.encodeToString(serializer(), this)
    }

    @Serializable
    data class TypeTag(
        @SerialName(ID_KEY)
        override val id: Long?,
        @SerialName(NAME_KEY)
        override val name: String,
        @SerialName(RELATIONS_KEY)
        override val relations: List<Relation>,
    ) : MDFileLanguagePart.LanguageTypeTag {
        @Serializable
        data class Relation(
            @SerialName(ID_KEY)
            override val id: Long? = null,
            @SerialName(NAME_KEY)
            override val name: String,
        ) : MDNameWithOptionalId {
            companion object Companion {
                const val ID_KEY = "id"// TODO, check serial name
                const val NAME_KEY = "relationLabel"// TODO, check serial name
            }
        }

        companion object Companion {
            const val ID_KEY = "id"// TODO, check serial name
            const val NAME_KEY = "typeTagName"// TODO, check serial name
            const val RELATIONS_KEY = "relations"// TODO, check serial name
        }

        override fun toTypeTag(language: Language): WordTypeTag = WordTypeTag(
            id = id ?: INVALID_ID,
            name = name,
            language = language,
            relations = relations.map { relation ->
                WordTypeTagRelation(
                    label = relation.name,
                    id = id ?: INVALID_ID,
                    wordsCount = 0
                )
            },
            wordsCount = 0,
        )
    }

    companion object Companion {
        const val CODE_KEY = "id"// TODO, check serial name
        const val TYPE_TAGS_KEY = "typeTags"// TODO, check serial name
    }
}
