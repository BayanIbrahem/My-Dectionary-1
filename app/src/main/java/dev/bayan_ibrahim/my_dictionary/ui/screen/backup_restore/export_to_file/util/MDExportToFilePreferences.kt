package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.simpleSerialize
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.simpleString
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum
import kotlinx.serialization.Serializable

@Serializable
sealed interface MDExportToFilePreferences : LabeledEnum {

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            All -> firstCapStringResource(R.string.all_x, firstCapStringResource(R.string.words))
            is Tags -> "${firstCapStringResource(R.string.tags)} (x${simpleSerializedTags.size})"
            is Languages -> "${firstCapStringResource(R.string.languages)} (x${codes.size})"
            is WordsClasses -> "${firstCapStringResource(R.string.word_classes)} (x${ids.size})"
            is Words -> "${firstCapStringResource(R.string.words)} (x${ids.size})"
        }

    data class Languages(val codes: Collection<LanguageCode>) : MDExportToFilePreferences

    /**
     * @param simpleSerializedTags simpleSerializedTags using [simpleSerialize]
     */
    data class Tags(val simpleSerializedTags: Collection<String>) : MDExportToFilePreferences {
        val tags: List<Tag>
            get() = simpleSerializedTags.map { Tag.simpleString(it) }
    }

    data class Words(val ids: Collection<Long>) : MDExportToFilePreferences
    data class WordsClasses(val ids: Collection<Long>) : MDExportToFilePreferences
    data object All : MDExportToFilePreferences
    companion object {
        val Default: MDExportToFilePreferences
            get() = All
    }
}