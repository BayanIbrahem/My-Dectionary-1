package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class WordsListTrainTarget(
    override val strLabel: String,
    override val icon: ImageVector,
) : LabeledEnum, IconedEnum {
    Meaning(
        strLabel = "On Meaning",
        icon = Icons.Default.Search
    ), // TODO, string res
    // TODO, icon res

    Translation(
        strLabel = "On Translation",
        icon = Icons.Default.Search
    ),// TODO, string res
    // TODO, icon res
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
