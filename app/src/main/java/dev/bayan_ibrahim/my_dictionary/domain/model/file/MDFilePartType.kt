package dev.bayan_ibrahim.my_dictionary.domain.model.file

import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDFilePartType(
    override val strLabel: String,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Language("Languages", MDIconsSet.LanguageWordSpace),
    Tag("Tags", MDIconsSet.WordTag),
    Word("Words", MDIconsSet.WordMeaning)
}
