package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class WordsListTrainTarget(
    override val strLabel: String,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Meaning(
        strLabel = "On Meaning",
        icon = MDIconsSet.WordMeaning 
    ), // TODO, string res

    Translation(
        strLabel = "On Translation",
        icon = MDIconsSet.WordTranslation 
    ),// TODO, string res
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
