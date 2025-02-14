package dev.bayan_ibrahim.my_dictionary.domain.model.count_enum

import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.QuantityEnum


@Suppress("EnumEntryName")
enum class WordsListTrainPreferencesLimit(override val quantity: Int) : QuantityEnum {
    _5(5),
    _10(10),
    _20(20),
    _25(25),
    _50(50),
    _75(75),
    _100(100);

    override val allStringRes: Int
        get() = R.string.words

    override val pluralsRes: Int
        get() = R.plurals.word
}
