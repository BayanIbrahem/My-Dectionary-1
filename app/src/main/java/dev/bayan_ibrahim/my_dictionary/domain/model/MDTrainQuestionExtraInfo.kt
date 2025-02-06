package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDTrainQuestionExtraInfo(
    override val strLabel: String,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Transcription("Transcription", MDIconsSet.WordTranscription),
    Tag("Tag", MDIconsSet.WordTag),
    WordClass("Word Class", MDIconsSet.WordClass),
    RelatedWords("Related Words", MDIconsSet.WordRelatedWords),
    Example("Examples", MDIconsSet.WordExample),
    AdditionalTranslation("Additional Translations", MDIconsSet.WordAdditionalTranslation),
}