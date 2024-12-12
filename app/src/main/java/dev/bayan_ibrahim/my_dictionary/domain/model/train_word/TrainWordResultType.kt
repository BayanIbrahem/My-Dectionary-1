package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class TrainWordResultType : LabeledEnum {
    Fail,
    Timeout,
    Pass;

    override val strLabel: String
        get() = when (this) {
            Fail -> "Failed"
            Timeout -> "Timeout"
            Pass -> "Passed"
        }
}