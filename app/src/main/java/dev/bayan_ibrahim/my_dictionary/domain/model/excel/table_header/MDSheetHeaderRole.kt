package dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum


sealed interface MDSheetHeaderRole : LabeledEnum {
    /**
     * this is the label of the header in the excel file
     */
    val excelLabel: String
    val required: Boolean

    /**
     * if true then this value is a list of string values
     */
    val isListValue: Boolean
}

/**
 * trim,
 * replace each white spaces sequences with underscore,
 * lowercase,
 * remove non english characters,
 * examples:
 *
 * ' WoRDS__- List - '  -> words_list
 *
 */
private fun String.normalizeExcelHeader(): String {
    return this.replace(
        oldValue = "[^\\w]+",
        newValue = "-"
    ).split('-').joinToString("_") { word ->
        word.lowercase()
    }
}

enum class MDSheetHeaderWordRole(
    override val excelLabel: String,
    override val required: Boolean = false,
    override val isListValue: Boolean = false,
) : MDSheetHeaderRole {
    LanguageCode(excelLabel = "language_code", required = true),
    Meaning(excelLabel = "meaning", required = true),
    Translation(excelLabel = "translation", required = true),
    Note(excelLabel = "note"),
    Transcription("transcription"),
    Tag("tag"),
    AdditionalTranslation("additional_translation"),
    Examples("examples"),
    WordClass("word_class"),
    RelatedWords("related_words"),
    Synonym("synonym"),
    Antonym("antonym"),
    Hyponym("hyponym"),
    Hypernym("hypernym"),
    Meronym("meronym"),
    Holonym("holonym"),
    Homonym("homonym"),
    Polysemy("polysemy"),
    Prototype("prototype"),
    Metonymy("Metonymy"),
    Collocation("collocation"),
    Homograph("homograph"),
    Homophone("homophone");


    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            // TODO, string res,
            else -> name
        }

    companion object {
        /**
         * this separator is used to split the related word label form the related word value
         */
        const val RELATED_WORD_SEPARATOR = "@"
        operator fun invoke(headerName: String): MDSheetHeaderWordRole? {
            val normalizedName = headerName.normalizeExcelHeader()
            return entries.firstOrNull { it.excelLabel == normalizedName }
        }
    }
}

enum class MDSheetHeaderTagRole(
    override val excelLabel: String,
    override val required: Boolean = false,
) : MDSheetHeaderRole {
    Color("color"),
    PassColor("pass_color"),
    Label("label", true),
    ParentTag("parent");

    override val isListValue: Boolean
        get() = false
    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            // TODO, string res,
            else -> ""
        }

    companion object {
        operator fun invoke(headerName: String): MDSheetHeaderTagRole? {
            val normalizedName = headerName.normalizeExcelHeader()
            return entries.firstOrNull { it.excelLabel == normalizedName }
        }
    }
}

enum class MDSheetHeaderWordClassRole(
    override val excelLabel: String,
    override val required: Boolean = false,
    override val isListValue: Boolean = false,
) : MDSheetHeaderRole {
    LanguageCode(excelLabel = "language_code", required = true, isListValue = false),
    WordClassName(excelLabel = "word_class", required = true, isListValue = false),
    WordClassRelationsNames(excelLabel = "relations", required = false, isListValue = true)
    ;

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            // TODO, string res,
            else -> name
        }

    companion object {
        operator fun invoke(headerName: String): MDSheetHeaderWordClassRole? {
            val normalizedName = headerName.normalizeExcelHeader()
            return entries.firstOrNull { it.excelLabel == normalizedName }
        }
    }
}