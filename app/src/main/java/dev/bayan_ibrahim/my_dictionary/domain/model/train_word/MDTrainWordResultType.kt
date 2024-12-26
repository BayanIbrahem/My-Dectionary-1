package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

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
            Pass -> MDIconsSet.Random // TODO, icon res
            Right -> MDIconsSet.Check // TODO, icon res
            Timeout -> MDIconsSet.CreateTime // TODO, icon res
            Wrong -> MDIconsSet.Close // TODO, icon res
        }

    override val strLabel: String
        get() = when (this) {
            Wrong -> "Wrong"
            Right -> "Right"
            Pass -> "Pass"
            Timeout -> "Timeout"
        }
}