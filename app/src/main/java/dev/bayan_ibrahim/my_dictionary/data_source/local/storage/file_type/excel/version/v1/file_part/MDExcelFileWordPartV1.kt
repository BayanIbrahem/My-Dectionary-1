package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileWordPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.StrIdentifiable
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.core.fire_part.MDExcelFileWordPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1.ExcelMapParseException
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import kotlinx.datetime.Clock
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord as ModelRelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass as ModelWordClass

/**
 * all init values are passed as strings
 */
class MDExcelFileWordPartV1(
    override val language: String,
    override val meaning: String,
    override val translation: String,
    override val transcription: String? = null,
    override val examples: List<String> = emptyList(),
    override val additionalTranslations: List<String> = emptyList(),
    tagsNames: List<String> = emptyList(),
    wordClass: String? = null,
    private val relatedWordsList: List<RelatedWord> = emptyList(),
    override val lexicalRelatedWords: List<LexicalRelation> = emptyList(),
) : MDExcelFileWordPart {
    override val tags: List<MDExcelFileTagPartV1> = tagsNames.map { MDExcelFileTagPartV1(it) }
    override val wordClass: WordClass? = wordClass?.let { WordClass(it) }
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
            tags = tags.mapNotNull { it.toTag() }.toSet(),
            transcription = transcription ?: "",
            examples = examples,
            wordClass = wordClass?.toModelWordClass(language)?.copy(
                relations = relatedWordsList.map {
                    WordClassRelation(
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

    data class WordClass(
        override val name: String,
    ) : StrIdentifiable {
        fun toModelWordClass(language: String): ModelWordClass {
            return ModelWordClass(
                id = INVALID_ID,
                name = name,
                language = language.code.getLanguage(),
                relations = emptyList(),
                wordsCount = 0,
            )
        }
    }

    data class RelatedWord(
        val languageCode: String,
        val wordClassName: String,
        override val name: String,
        val relatedWord: String,
    ) : StrIdentifiable

    data class LexicalRelation(
        override val type: WordLexicalRelationType,
        override val value: String,
    ) : MDFileWordPart.LexicalRelation

    companion object {
        const val COLUMN_LANGUAGE_CODE = "language_code"
        const val COLUMN_MEANING = "word_meaning"
        const val COLUMN_TRANSLATION = "word_translation"
        const val COLUMN_TRANSCRIPTION = "word_transcription"
        const val COLUMN_WORD_CLASS = "class_of_word"
        const val COLUMN_TAGS = "word_tags"
        const val COLUMN_EXAMPLES = "word_examples"
        const val COLUMN_ADDITIONAL_TRANSLATIONS = "word_additional_translations"

        /**
         * Creates an `MDExcelFileWordPartV1` instance from a map.
         *
         * @param map The map containing the word data.
         * @param defaultValues Optional map of default values.
         * @param keyCustomMapper Optional map to customize key mappings.
         * @return An `MDExcelFileWordPartV1` instance.
         * @throws ExcelMapParseException if required keys are missing.
         *
         * Example:
         * ```kotlin
         * val map = mapOf(
         * "custom_language" to "en",
         * "custom_meaning" to "apple",
         * "custom_translation" to "تفاحة",
         * "transcription" to "[æpl]",
         * "word_class" to "noun",
         * "examples" to "I ate an apple.",
         * "additional_translations" to "تفاح",
         * "lexical_relations" to "antonym=banana,synonym=fruit"
         * )
         * val customMapper = mapOf(COLUMN_LANGUAGE to "custom_language", COLUMN_MEANING to "custom_meaning", COLUMN_TRANSLATION to "custom_translation")
         * val wordPart = MDExcelFileWordPartV1(map, keyCustomMapper = customMapper)
         * ```
         */
        operator fun invoke(
            map: Map<String, String>,
            onGetRelatedWords: (language: String, meaning: String, wordClass: String) -> List<RelatedWord>,
            defaultValues: Map<String, String> = emptyMap(),
            keyCustomMapper: Map<String, String> = emptyMap(),
        ): MDExcelFileWordPartV1 {
            val languageKey = keyCustomMapper[COLUMN_LANGUAGE_CODE] ?: COLUMN_LANGUAGE_CODE
            val language = map[languageKey] ?: defaultValues[COLUMN_LANGUAGE_CODE] ?: throw ExcelMapParseException(
                objectName = "Word Part",
                requiredKey = COLUMN_LANGUAGE_CODE,
                providedMap = map
            )

            val meaningKey = keyCustomMapper[COLUMN_MEANING] ?: COLUMN_MEANING
            val meaning = map[meaningKey] ?: defaultValues[COLUMN_MEANING] ?: throw ExcelMapParseException(
                objectName = "Word Part",
                requiredKey = COLUMN_MEANING,
                providedMap = map
            )

            val translationKey = keyCustomMapper[COLUMN_TRANSLATION] ?: COLUMN_TRANSLATION
            val translation = map[translationKey] ?: defaultValues[COLUMN_TRANSLATION] ?: throw ExcelMapParseException(
                objectName = "Word Part",
                requiredKey = COLUMN_TRANSLATION,
                providedMap = map
            )

            val transcription = map[keyCustomMapper[COLUMN_TRANSCRIPTION] ?: COLUMN_TRANSCRIPTION] ?: defaultValues[COLUMN_TRANSCRIPTION]
            val wordClass = map[keyCustomMapper[COLUMN_WORD_CLASS] ?: COLUMN_WORD_CLASS] ?: defaultValues[COLUMN_WORD_CLASS]
            val examples = map[keyCustomMapper[COLUMN_EXAMPLES] ?: COLUMN_EXAMPLES]?.lines() ?: defaultValues[COLUMN_EXAMPLES]?.lines() ?: emptyList()
            val additionalTranslations = map[keyCustomMapper[COLUMN_ADDITIONAL_TRANSLATIONS] ?: COLUMN_ADDITIONAL_TRANSLATIONS]?.lines()
                ?: defaultValues[COLUMN_ADDITIONAL_TRANSLATIONS]?.lines() ?: emptyList()

            //lexical relation split
            val customKeys = keyCustomMapper.mapKeys { (key, _) ->
                if (WordLexicalRelationType.validKey(key)
                ) {
                    key.lowercase()
                } else {
                    null
                }
            }
            /// contains a map of all lexical relations values
            val lexicalRelatedWords = WordLexicalRelationType.entries.mapNotNull { relation ->
                val name = relation.name.lowercase()
                val key = customKeys[name] ?: name
                val values = (map[key] ?: defaultValues[name])?.lines()
                if (values == null) return@mapNotNull null
                values.map { value ->
                    LexicalRelation(relation, value)
                }
            }.flatten()
            val tagsNames = map[keyCustomMapper[COLUMN_TAGS] ?: COLUMN_TAGS]?.lines() ?: defaultValues[COLUMN_TAGS]?.lines() ?: emptyList()

            return MDExcelFileWordPartV1(
                language = language,
                meaning = meaning,
                translation = translation,
                transcription = transcription,
                examples = examples,
                additionalTranslations = additionalTranslations,
                tagsNames = tagsNames,
                wordClass = wordClass,
                relatedWordsList = if (wordClass.isNullOrBlank()) emptyList() else onGetRelatedWords(language, meaning, wordClass),
                lexicalRelatedWords = lexicalRelatedWords,
            )
        }
    }
}
