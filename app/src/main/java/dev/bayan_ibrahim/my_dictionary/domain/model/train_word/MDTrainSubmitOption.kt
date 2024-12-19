package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class MDTrainSubmitOption(
    override val strLabel: String,
    override val icon: MDIconsSet,
    val primary: Boolean,
) : LabeledEnum, IconedEnum {
    Pass("Pass", icon = MDIconsSet.Check, false), // TODO, icon res, string res
    Guess("Guess", icon = MDIconsSet.Check, true), // TODO, icon res, string res
    Answer("Answer", icon = MDIconsSet.Check, true), // TODO, icon res, string res
    Confident("Confident", icon = MDIconsSet.Check, true); // TODO, icon res, string res

    companion object Companion {
        val primaryEntities = entries.filter { it.primary }
        val secondaryEntities = entries.filterNot { it.primary }
    }
}