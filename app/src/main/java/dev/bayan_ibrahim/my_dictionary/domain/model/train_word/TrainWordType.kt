package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class TrainWordType(
    override val strLabel: String,
    override val icon: MDIconsSet,
    /**
     * global key for this type, used for database to keep correct types after migration
     */
    val key: Int,
) : LabeledEnum, IconedEnum {
    SelectWordMeaning(
        strLabel = "Select Word",
        icon = MDIconsSet.WordTrainSelectType, // checked
        key = 0
    ), // TODO, string res

    WriteWord(
        strLabel = "Write Word",
        icon = MDIconsSet.WordTrainWriteType, // checked
        key = 2,
    ); // TODO, string res

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
         * number of items that we pick according to tags similarity before taking an [MAX_SELECTIONS_COUNT] random item
         */
        const val SELECTION_TAGS_GROUP_COUNT: Int = MAX_SELECTIONS_COUNT * 5

        /**
         * number of items that we pick according to words similarity before taking an [MAX_SELECTIONS_COUNT] random item
         */
        const val SELECTION_LEVENSHTEIN_GROUP_COUNT: Int = MAX_SELECTIONS_COUNT * 5
    }
}