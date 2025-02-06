package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format

import java.text.DecimalFormat


fun Float.asSimpleString(
    floatingPoint: Int = 1,
): String {
    return DecimalFormat(
        "0." + "#".repeat(maxOf(0, floatingPoint))
    ).format(this)
}