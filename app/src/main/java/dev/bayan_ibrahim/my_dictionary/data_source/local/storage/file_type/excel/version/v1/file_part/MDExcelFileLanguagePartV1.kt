package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.StrIdentifiable
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.core.fire_part.MDExcelFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1.ExcelMapParseException
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass as ModelWordClass

/**
 * it would be like a table with 3 columns
 * language code, word class name, relations
 *
 * en	name	"singlular
 *                plural"
 * en	verb	   "v1
 *                  v2
 *                  v3"
 *
 */
data class MDExcelFileLanguagePartV1(
    override val code: String,
    override val wordsClasses: List<WordClass>,
) : MDExcelFileLanguagePart {
    override fun toLanguage(): Language = code.code.getLanguage()

    data class WordClass(
        val code: String,
        override val name: String,
        val rawRelations: List<String>,
    ) : MDFileLanguagePart.LanguageWordClass {
        constructor(
            code: String,
            name: String,
            rawRelations: String,
        ) : this(
            code = code,
            name = name,
            rawRelations = rawRelations.lines()
        )

        /**
         * require [cellsValues] length is 3
         * if [combineTrailingItemsToRelations] is true then each value 3rd+ would be a relation
         */
        constructor(
            cellsValues: List<String>,
            combineTrailingItemsToRelations: Boolean = true,
        ) : this(
            cellsValues[0],
            cellsValues[1],
            cellsValues.subList(
                fromIndex = 2,
                toIndex = if (combineTrailingItemsToRelations) cellsValues.size else 3
            ).map {
                it.lines()
            }.flatten(),
        )

        override val relations: List<Relation> = rawRelations.mapNotNull { relationName ->
            if (relationName.isBlank()) {
                null
            } else {
                Relation(relationName)
            }
        }

        data class Relation(
            override val name: String,
        ) : StrIdentifiable

        override fun toWordClass(language: Language): ModelWordClass = ModelWordClass(
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

        companion object {
            const val COLUMN_LANGUAGE_CODE = "language_code"
            const val COLUMN_WORD_CLASS_NAME = "word_class_name"
            const val COLUMN_WORD_CLASS_RELATION_NAME = "word_class_relation_name"

            /**
             * @param keyCustomMapper if some keys have different values from default pass them here
             * @param defaultValues if you have some default values like language pass them in ,
             * **NOTE, DEFAULT VALUES MAP KEYS ARE THE ORIGINAL KEYS ONLY NOT THE CUSTOM ONES**
             * @throws ExcelMapParseException
             * make the custom key is the key in the helper map and the value is the original key
             * e.g
             * ```
             * //original keys: code, name.
             * current map:
             * {
             *     code: value,
             *     my_name: value,
             * }
             * custom mapper:
             * {
             *     name: my_name,
             * }
             * ```
             */
            operator fun invoke(
                map: Map<String, String>,
                defaultValues: Map<String, String> = emptyMap(),
                keyCustomMapper: Map<String, String> = emptyMap(),
            ): WordClass {
                val codeKey = keyCustomMapper[COLUMN_LANGUAGE_CODE] ?: COLUMN_LANGUAGE_CODE
                val code = map[codeKey] ?: defaultValues[COLUMN_LANGUAGE_CODE] ?: throw ExcelMapParseException(
                    objectName = "Language word class",
                    requiredKey = COLUMN_LANGUAGE_CODE,
                    providedMap = map
                )
                val nameKey = keyCustomMapper[COLUMN_WORD_CLASS_NAME] ?: COLUMN_WORD_CLASS_NAME
                val name = map[nameKey] ?: defaultValues[COLUMN_WORD_CLASS_NAME] ?: throw ExcelMapParseException(
                    objectName = "Language word class",
                    requiredKey = COLUMN_WORD_CLASS_NAME,
                    providedMap = map
                )
                val relationsKey = keyCustomMapper[COLUMN_WORD_CLASS_RELATION_NAME] ?: COLUMN_WORD_CLASS_RELATION_NAME
                val relations = map[relationsKey] ?: defaultValues[COLUMN_WORD_CLASS_RELATION_NAME] ?: ""

                return WordClass(code = code, name = name, rawRelations = relations)
            }
        }
    }
}

