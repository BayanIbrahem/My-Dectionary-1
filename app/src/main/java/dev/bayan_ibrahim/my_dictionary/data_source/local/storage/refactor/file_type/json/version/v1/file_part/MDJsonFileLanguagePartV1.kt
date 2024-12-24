package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDNameWithOptionalId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MDJsonFileLanguagePartV1(
    @SerialName(CODE_KEY)
    override val code: String,
    @SerialName(TYPE_TAGS_KEY)
    override val typeTags: List<TypeTag>,
) : dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_part.MDJsonFileLanguagePart {
    @Transient
    override val version: Int = 1

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
    }

    companion object Companion {
        const val CODE_KEY = "id"// TODO, check serial name
        const val TYPE_TAGS_KEY = "typeTags"// TODO, check serial name
    }
}
