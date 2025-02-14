package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDTrainWordResultType : LabeledEnum, IconedEnum {
    Wrong,
    Right,
    Pass,
    Timeout;

    override val icon: MDIconsSet
        get() = when (this) {
            Pass -> MDIconsSet.Random
            Right -> MDIconsSet.Check
            Timeout -> MDIconsSet.CreateTime
            Wrong -> MDIconsSet.Close
        }

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Wrong -> firstCapStringResource(R.string.wrong)
            Right -> firstCapStringResource(R.string.right)
            Pass -> firstCapStringResource(R.string.pass)
            Timeout -> firstCapStringResource(R.string.timeout)
        }
}