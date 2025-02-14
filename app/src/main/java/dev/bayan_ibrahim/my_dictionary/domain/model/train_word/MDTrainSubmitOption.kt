package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDTrainSubmitOption(
    @StringRes val labelRes: Int,
    override val icon: MDIconsSet,
    val primary: Boolean,
) : LabeledEnum, IconedEnum {
    Pass(R.string.pass, icon = MDIconsSet.Check, false),
    Guess(R.string.guess, icon = MDIconsSet.Check, true),
    Answer(R.string.answer, icon = MDIconsSet.Check, true),
    Confident(R.string.confident, icon = MDIconsSet.Check, true);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)

    companion object Companion {
        val primaryEntities = entries.filter { it.primary }
        val secondaryEntities = entries.filterNot { it.primary }
    }
}