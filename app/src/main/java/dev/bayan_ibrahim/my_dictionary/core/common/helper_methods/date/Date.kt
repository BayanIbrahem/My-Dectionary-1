package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

fun LocalDate.atDefaultStartOfDay() = this.atStartOfDayIn(TimeZone.currentSystemDefault())

fun LocalDate.toEpochQuarters(): Int = year * 4 + month.ordinal.div(3)
fun LocalDate.toEpochMonths(): Int = year * 12 + month.ordinal
private val JAN_1_1970_DAY_OF_WEEK = DayOfWeek.THURSDAY

// M T W T F S S
// 0 1 2 3 4 5 6
// - - - * 1 2 3
// 4 5 6 7 8 9 10
fun LocalDate.toEpochWeeks(
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
): Int {
    val epochDays = toEpochDays()
    return if (startOfWeek == JAN_1_1970_DAY_OF_WEEK) {
        // thursday
        epochDays
    } else if (startOfWeek > JAN_1_1970_DAY_OF_WEEK) {
        // friday, saturday, sunday
        epochDays.plus(startOfWeek.ordinal)
    } else {
        // monday, tuesday, wednesday
        epochDays.plus(JAN_1_1970_DAY_OF_WEEK.ordinal).minus(startOfWeek.ordinal)
    }.div(7)
}

/**
 * return start for first day of week according to [startOfWeek]
 */
fun LocalDate.Companion.fromEpochWeeks(
    epochWeeks: Int,
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
): LocalDate {
    // [0..6] -> {3, 2, 1, 0, 6, 5, 4}
    val factor = 10.minus(startOfWeek.ordinal).mod(7)
    val epochDays = epochWeeks.times(7).minus(factor)
    return fromEpochDays(epochDays)
}


