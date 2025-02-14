package dev.bayan_ibrahim.my_dictionary.domain.model.count_enum

import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.QuantityEnum

@Suppress("EnumEntryName")
enum class MDStatisticsMostResentHistoryCount(override val quantity: Int) : QuantityEnum {
    _1(1),
    _2(2),
    _3(3),
    _5(5),
    _10(10),
    _15(15),
    _25(25),
    _50(50),
    _100(100),
    All(Int.MAX_VALUE);

    override val allStringRes: Int
        get() = R.string.statistics
    override val pluralsRes: Int
        get() = R.plurals.train_history

}
