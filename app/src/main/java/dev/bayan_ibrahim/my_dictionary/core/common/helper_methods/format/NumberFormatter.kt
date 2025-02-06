package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format

import java.text.DecimalFormat
import kotlin.math.absoluteValue
import kotlin.math.pow


/*
E0 -
E3  thousand
E6  million
E9  billion
E12	trillion
E15	quadrillion
E18	quintillion
E21	sextillion
E24	septillion
E27	octillion
E30	nonillion
E33	decillion
E36	undecillion
E39	duodecillion
E42	tredecillion
E45	quattuordecillion
 */
private val groups = listOf(
    "", "K", "M", "B", "T", "Qd"
)

@Suppress("EnumEntryName")
enum class NumberFormatGroup(val sfx: String) {
    x1(""),
    x1_000("K"),
    x1_000_000("M"),
    x1_000_000_000("B"),
    x1_000_000_000_000("T"),
    x1_000_000_000_000_000("QD")
}

fun Long.asFormattedString(
    getSuffix: (NumberFormatGroup) -> String = { it.sfx },
): String {
    //             1 -> 1
    //            10 -> 10
    //           100 -> 0.1k
    //         1 000 -> 1k
    //        10 000 -> 10k
    //       100 000 -> 0.1m
    //     1 000 000 -> 1m
    //    10 000 000 -> 10m
    //   100 000 000 -> 0.1b
    // 1 000 100 000 -> 1b
    // 610
    val len = absoluteValue.toString().length
    val negative = this < 0
    val group = NumberFormatGroup.entries[(len / 3).coerceIn(0, 5)]
    val sfx = getSuffix(group)

    val power = (group.ordinal.times(3)).coerceAtLeast(0)
    val factor = 10.0.pow(power)

    return buildString {
        if (negative) {
            append("-")
        }
        append(DecimalFormat("0.#").format(this@asFormattedString / factor))
        append(sfx)
    }
}

fun Number.asFormattedString(
    getSuffix: (NumberFormatGroup) -> String = { it.sfx },
): String = toLong().asFormattedString(getSuffix)
