package dev.bayan_ibrahim.my_dictionary.domain.model.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.MDDateTimeFormat
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.atDefaultStartOfDay
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.format
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.fromEpochWeeks
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.now
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toDefaultLocalDateTime
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toEpochMonths
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toEpochQuarters
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toEpochWeeks
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.time.format.TextStyle
import java.util.Locale

enum class MDDateUnit {
    /** four quarters */
    Year,

    /** 3 months */
    Quarter,

    /** 4 weeks */
    Month,

    /** 7 days */
    Week,

    /** 24 hours */
    Day;
}

fun MDDateUnit.toDatetimeUnit(): DateTimeUnit.DateBased = when (this) {
    MDDateUnit.Year -> DateTimeUnit.YEAR
    MDDateUnit.Quarter -> DateTimeUnit.QUARTER
    MDDateUnit.Month -> DateTimeUnit.MONTH
    MDDateUnit.Week -> DateTimeUnit.WEEK
    MDDateUnit.Day -> DateTimeUnit.DAY
}

fun MDDateUnit.instantIdentifier(instant: Instant): Int {
    val datetime = instant.toDefaultLocalDateTime()
    return when (this) {
        MDDateUnit.Year -> datetime.date.toEpochQuarters()
        MDDateUnit.Quarter -> datetime.date.toEpochMonths()
        MDDateUnit.Month -> datetime.date.toEpochWeeks()
        MDDateUnit.Week -> datetime.date.toEpochDays()
        MDDateUnit.Day -> datetime.time.hour
    }
}

@Composable
fun MDDateUnit.labelOfIdentifier(
    identifier: Int,
    dateFormat: (LocalDate) -> String = {
        it.format(MDDateTimeFormat.AbbreviationNoYear)
    },
): String {
    return when (this) {
        MDDateUnit.Year -> {
            when (identifier.mod(4)) { // TODO, string res
                0 -> "Spring"
                1 -> "Summer"
                2 -> "Atom"
                else -> "Winter"
            }
        }

        MDDateUnit.Quarter -> {
            val monthOfYear = Month.entries[identifier.mod(12)]
            monthOfYear.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
        }

        MDDateUnit.Month -> {
            val startOfWeek = LocalDate.fromEpochWeeks(identifier)
            val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)
            val s = startOfWeek.format(MDDateTimeFormat.AbbreviationMonth)
            val e = if (startOfWeek.month == endOfWeek.month) {
                endOfWeek.dayOfMonth
            } else {
                endOfWeek.format(MDDateTimeFormat.AbbreviationMonth)
            }

            // same month: Jun 11-18
            // different month: Jun 30-Jul 5
            "$s-$e"
        }

        MDDateUnit.Week -> {
            val date = LocalDate.fromEpochDays(identifier)
            val currentMonth = LocalDateTime.now().month
            if (date.month == currentMonth) {
                date.dayOfMonth.toString()
            } else {
                date.format(MDDateTimeFormat.AbbreviationMonth)
            }
        }

        MDDateUnit.Day -> {
            val time = LocalTime(identifier, 0, 0)
            time.format(MDDateTimeFormat.TimeOnly12)
        }
    }
}

val MDDateUnit.label: String
@Composable
@ReadOnlyComposable
get() = when (this) {
    MDDateUnit.Year -> "Year"
    MDDateUnit.Quarter -> "Quarter (season)"
    MDDateUnit.Month -> "Month"
    MDDateUnit.Week -> "Week"
    MDDateUnit.Day -> "Day"
}
fun MDDateUnit.startOf(endInstant: Instant): Instant {
    val datetime = endInstant.toDefaultLocalDateTime()
    val startDate = datetime.date.minus(1, toDatetimeUnit())
    return startDate.atDefaultStartOfDay()
}
