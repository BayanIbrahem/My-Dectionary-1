package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class TrainWordResultType : LabeledEnum {
    Wrong,
    Right,
    Pass,
    Timeout;

    override val strLabel: String
        get() = when (this) {
            Wrong -> "Wrong"
            Right -> "Right"
            Pass -> "Pass"
            Timeout -> "Timeout"
        }
}