package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class WordsListTrainTarget(
    @StringRes val labelRes: Int,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Meaning(
        labelRes = R.string.meaning,
        icon = MDIconsSet.WordMeaning 
    ),

    Translation(
        labelRes = R.string.translation,
        icon = MDIconsSet.WordTranslation 
    );

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(R.string.on_x, firstCapStringResource(labelRes))
}

val WordsListTrainTarget.questionSelector
    get() = when (this) {
        WordsListTrainTarget.Meaning -> { word: Word -> word.meaning }
        WordsListTrainTarget.Translation -> { word: Word -> word.translation }
    }
val WordsListTrainTarget.answerSelector
    get() = when (this) {
        WordsListTrainTarget.Meaning -> { word: Word -> word.translation }
        WordsListTrainTarget.Translation -> { word: Word -> word.meaning }
    }
