package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.StrIdentifiable
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
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
    @SerialName(WORDS_CLASSES_KEY)
    override val wordsClasses: List<WordClass>,
) : MDJsonFileLanguagePart {
    override fun toLanguage(): Language = code.code.getLanguage()

    override suspend fun jsonStringify(json: Json): String {
        return json.encodeToString(serializer(), this)
    }

    @Serializable
    data class WordClass(
        @SerialName(NAME_KEY)
        override val name: String,
        @SerialName(RELATIONS_KEY)
        override val relations: List<Relation>,
    ) : MDFileLanguagePart.LanguageWordClass {
        @Serializable
        data class Relation(
            @SerialName(NAME_KEY)
            override val name: String,
        ) : StrIdentifiable {
            companion object Companion {
                const val NAME_KEY = "relationLabel"
            }
        }

        companion object Companion {
            const val NAME_KEY = "wordClassName"
            const val RELATIONS_KEY = "relations"
        }

        override fun toWordClass(language: Language): WordClass = WordClass(
            id = INVALID_ID,
            name = name,
            language = language,
            relations = relations.map { relation ->
                WordClassRelation(
                    label = relation.name,
                    wordsCount = 0
                )
            },
            wordsCount = 0,
        )
    }

    companion object Companion {
        const val CODE_KEY = "id"
        const val WORDS_CLASSES_KEY = "wordsClasses"
    }
}
