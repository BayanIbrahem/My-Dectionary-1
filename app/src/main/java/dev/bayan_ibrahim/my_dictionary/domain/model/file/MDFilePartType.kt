package dev.bayan_ibrahim.my_dictionary.domain.model.file

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDFilePartType(
    @StringRes val labelRes: Int,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Language(R.string.languages, MDIconsSet.LanguageWordSpace),
    Tag(R.string.tags, MDIconsSet.WordTag),
    Word(R.string.words, MDIconsSet.WordMeaning);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)
}
