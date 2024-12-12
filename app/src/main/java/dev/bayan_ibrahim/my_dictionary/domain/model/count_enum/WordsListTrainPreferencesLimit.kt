package dev.bayan_ibrahim.my_dictionary.domain.model.count_enum

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.CountEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum


@Suppress("EnumEntryName")
enum class WordsListTrainPreferencesLimit(override val count: Int) : CountEnum {
    _5(5),
    _10(10),
    _20(20),
    _25(25),
    _50(50),
    _75(75),
    _100(100);
}
