package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.util

import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum
import kotlinx.serialization.Serializable

@Serializable
sealed interface MDExportToFilePreferences : LabeledEnum {
    override val strLabel: String
        get() = when (this) {
            All -> "All Words"
            is ContextTags -> "Context Tags (x${ids.size})"
            is Languages -> "Languages (x${codes.size})"
            is TypeTags -> "TypeTags (x${ids.size})"
            is Words -> "Words (x${ids.size})"
        }

    data class Languages(val codes: Collection<LanguageCode>) : MDExportToFilePreferences
    data class ContextTags(val ids: Collection<Long>) : MDExportToFilePreferences
    data class Words(val ids: Collection<Long>) : MDExportToFilePreferences
    data class TypeTags(val ids: Collection<Long>) : MDExportToFilePreferences
    data object All : MDExportToFilePreferences
    companion object {
        val Default: MDExportToFilePreferences
            get() = All
    }
}