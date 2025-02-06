package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.util

import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.simpleSerialize
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.simpleString
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum
import kotlinx.serialization.Serializable

@Serializable
sealed interface MDExportToFilePreferences : LabeledEnum {
    override val strLabel: String
        get() = when (this) {
            All -> "All Words"
            is ContextTags -> "Context Tags (x${simpleSerializedTags.size})"
            is Languages -> "Languages (x${codes.size})"
            is WordsClasses -> "WordsClasses (x${ids.size})"
            is Words -> "Words (x${ids.size})"
        }

    data class Languages(val codes: Collection<LanguageCode>) : MDExportToFilePreferences

    /**
     * @param simpleSerializedTags simpleSerializedTags using [simpleSerialize]
     */
    data class ContextTags(val simpleSerializedTags: Collection<String>) : MDExportToFilePreferences {
        val tags: List<ContextTag>
            get() = simpleSerializedTags.map { ContextTag.simpleString(it) }
    }

    data class Words(val ids: Collection<Long>) : MDExportToFilePreferences
    data class WordsClasses(val ids: Collection<Long>) : MDExportToFilePreferences
    data object All : MDExportToFilePreferences
    companion object {
        val Default: MDExportToFilePreferences
            get() = All
    }
}