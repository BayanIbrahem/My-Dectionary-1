package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1.file_part

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.core.fire_part.MDExcelFileTagPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.version.v1.ExcelMapParseException
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagSegmentSeparator
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.validate
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.strHex

/**
 * tag is like a table, it may has more than one column for its name levels
 * require [name] segments is separated by [TagSegmentSeparator]
 */
data class MDExcelFileTagPartV1(
    override val name: String,
    override val color: String? = null,
    override val passColorToChildren: Boolean? = null,
) : MDExcelFileTagPart {
    constructor(
        nameSegments: List<String>,
        color: String? = null,
        passColorToChildren: Boolean? = null,
    ): this(
        name = nameSegments.joinToString(TagSegmentSeparator),
        color = color,
        passColorToChildren = passColorToChildren
    )
    override fun toTag(): Tag? = name.takeIf {
        it.isNotBlank()
    }?.let {
        Tag(
            value = this.name,
            color = this.color?.let { Color.strHex(it) },
            passColorToChildren = this.passColorToChildren == true,
            currentColorIsPassed = false,
        ).validate()
    }

    companion object {
        const val COLUMN_TAG_NAME = "tag_name"
        const val COLUMN_TAG_COLOR = "tag_color"
        const val COLUMN_TAG_PASS_COLOR = "tag_pass_color"

        /**
         * Creates an `MDExcelFileTagPartV1` instance from a map.
         *
         * @param map The map containing the tag data.
         * @param defaultValues Optional map of default values.
         * @param keyCustomMapper Optional map to customize key mappings.
         * @return An [MDExcelFileTagPartV1] instance.
         * @throws ExcelMapParseException if required keys are missing.
         *
         * Example:
         * ```kotlin
         * val map = mapOf(
         * "custom_name" to "tag1" + TagSegmentSeparator + "tag2",
         * "color" to "#FFFFFF",
         * "pass_color" to "true"
         * )
         * val customMapper = mapOf(COLUMN_TAG_NAME to "custom_name", COLUMN_TAG_PASS_COLOR_TO_CHILDREN to "pass_color")
         * val tagPart = MDExcelFileTagPartV1(map, keyCustomMapper = customMapper)
         * ```
         */
        operator fun invoke(
            map: Map<String, String>,
            defaultValues: Map<String, String> = emptyMap(),
            keyCustomMapper: Map<String, String> = emptyMap(),
        ): MDExcelFileTagPartV1 {
            val nameKey = keyCustomMapper[COLUMN_TAG_NAME] ?: COLUMN_TAG_NAME
            val name = map[nameKey] ?: defaultValues[COLUMN_TAG_NAME] ?: throw ExcelMapParseException(
                objectName = "Tag Part",
                requiredKey = COLUMN_TAG_NAME,
                providedMap = map
            )

            val colorKey = keyCustomMapper[COLUMN_TAG_COLOR] ?: COLUMN_TAG_COLOR
            val color = map[colorKey] ?: defaultValues[COLUMN_TAG_COLOR]

            val passColorToChildrenKey = keyCustomMapper[COLUMN_TAG_PASS_COLOR] ?: COLUMN_TAG_PASS_COLOR
            val passColorToChildren = (map[passColorToChildrenKey] ?: defaultValues[COLUMN_TAG_PASS_COLOR])?.lowercase()?.toBooleanStrictOrNull()

            return MDExcelFileTagPartV1(
                name = name,
                color = color,
                passColorToChildren = passColorToChildren
            )
        }
    }
}
