package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class TrainWordType(
    override val strLabel: String,
    override val icon: ImageVector,
    /**
     * global key for this type, used for database to keep correct types after migration
     */
    val key: Int,
) : LabeledEnum, IconedEnum {
    SelectWordMeaning(
        strLabel = "Select Word (Meaning)",
        icon = Icons.Default.ThumbUp,
        key = 0
    ) , // TODO, string res

    // TODO icon res
    WriteWord(
        strLabel = "Write Word",
        icon = Icons.Default.ThumbUp,
        key = 2,
    ); // TODO, string res
    // TODO icon res

    companion object Companion {
        /**
         * number of selections in [SelectWordMeaning]
         */
        const val MAX_SELECTIONS_COUNT: Int = 4

        /**
         * minimum valid number of selections in [SelectWordMeaning]
         */
        const val MIN_SELECTIONS_COUNT: Int = 4

        /**
         * number of items that we pick according to tags similarity before taking an [MAX_SELECTIONS_COUNT] random dev.bayan_ibrahim.my_dictionary.core.design_system.group.item
         */
        const val SELECTION_TAGS_GROUP_COUNT: Int = MAX_SELECTIONS_COUNT * 5

        /**
         * number of items that we pick according to words similarity before taking an [MAX_SELECTIONS_COUNT] random dev.bayan_ibrahim.my_dictionary.core.design_system.group.item
         */
        const val SELECTION_LEVENSHTEIN_GROUP_COUNT: Int = MAX_SELECTIONS_COUNT * 5
    }
}