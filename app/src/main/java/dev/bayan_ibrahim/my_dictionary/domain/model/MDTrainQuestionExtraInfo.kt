package dev.bayan_ibrahim.my_dictionary.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDTrainQuestionExtraInfo(
    @StringRes
    val labelRes: Int,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Transcription(R.string.transcription, MDIconsSet.WordTranscription),
    Tag(R.string.tag, MDIconsSet.WordTag),
    WordClass(R.string.word_class, MDIconsSet.WordClass),
    RelatedWords(R.string.related_word, MDIconsSet.WordRelatedWords),
    Example(R.string.example, MDIconsSet.WordExample),
    AdditionalTranslation(R.string.additional_translation, MDIconsSet.WordAdditionalTranslation);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)
}