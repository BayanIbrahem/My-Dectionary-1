package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class WordsListTrainType(
    override val strLabel: String,
    override val icon: ImageVector,
) : LabeledEnum, IconedEnum {
    SelectWordMeaning(
        strLabel = "Select Word (Meaning)",
        icon = Icons.Default.ThumbUp
    ), // TODO, string res

    // TODO icon res
    SelectWordSpelling(
        strLabel = "Select Word (Spelling)",
        icon = Icons.Default.ThumbUp
    ), // TODO, string res

    // TODO icon res
    WriteWord(
        strLabel = "Write Word",
        icon = Icons.Default.ThumbUp
    ); // TODO, string res
    // TODO icon res
}