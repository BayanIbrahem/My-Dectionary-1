package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class TrainWordType(
    @StringRes val labelRes: Int,
    override val icon: MDIconsSet,
    /**
     * global key for this type, used for database to keep correct types after migration
     */
    val key: Int,
) : LabeledEnum, IconedEnum {
    SelectWordMeaning(
        labelRes = R.string.select_word,
        icon = MDIconsSet.WordTrainSelectType, 
        key = 0
    ),

    WriteWord(
        labelRes = R.string.write_word,
        icon = MDIconsSet.WordTrainWriteType, 
        key = 2,
    );

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)

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