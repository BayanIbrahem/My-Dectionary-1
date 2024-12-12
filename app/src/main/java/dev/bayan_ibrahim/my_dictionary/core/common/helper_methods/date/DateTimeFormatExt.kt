@file:Suppress("SpellCheckingInspection")

package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@JvmName("LocalDateFormatEnum")
fun LocalDate.format(
    format: MDDateTimeFormat,
    locale: Locale = Locale.getDefault(),
): String {
    return format(format.pattern, locale)
}

@JvmName("LocalTimeFormatEnum")
fun LocalTime.format(
    format: MDDateTimeFormat,
    locale: Locale = Locale.getDefault(),
): String {
    return format(format.pattern, locale)
}

@JvmName("LocalDateTimeFormatEnum")
fun LocalDateTime.format(
    format: MDDateTimeFormat,
    locale: Locale = Locale.getDefault(),
): String {
    return format(format.pattern, locale)
}

@JvmName("LocalDateFormatString")
fun LocalDate.format(
    pattern: String,
    locale: Locale = Locale.getDefault(),
): String = format(pattern.mapDateSymbolsToArgs(), locale)

@JvmName("LocalTimeFormatString")
fun LocalTime.format(
    pattern: String,
    locale: Locale = Locale.getDefault(),
): String = format(pattern.mapDateSymbolsToArgs(), locale)

@JvmName("LocalDateTimeFormatString")
fun LocalDateTime.format(
    pattern: String,
    locale: Locale = Locale.getDefault(),
): String = format(pattern.mapDateSymbolsToArgs(), locale)

@JvmName("LocalDateFormatPattern")
fun LocalDate.format(
    pattern: DateTimeFormatPattern,
    locale: Locale = Locale.getDefault(),
): String = pattern.format(asDateTimeSymbolsArgs(locale))

@JvmName("LocalTimeFormatPattern")
fun LocalTime.format(
    pattern: DateTimeFormatPattern,
    locale: Locale = Locale.getDefault(),
): String = pattern.format(asDateTimeSymbolsArgs(locale))

@JvmName("LocalDateTimeFormatPattern")
fun LocalDateTime.format(
    pattern: DateTimeFormatPattern,
    locale: Locale = Locale.getDefault(),
): String = pattern.format(asDateTimeSymbolsArgs(locale))

private fun DateTimeFormatPattern.format(args: Array<String>) = value.format(*args)

/*
 Year:
 y or yy: Year without a century (e.g., 23 for 2023)
 yyyy: Year with century (e.g., 2023)
 Month:
 M or mm: Month as a number (1-12, padded with leading zero if needed)
 MMM: Abbreviated month name (e.g., Jun for June)
 MMMM: Full month name (e.g., June)
 Day:
 d or dd: Day of the month (1-31, padded with leading zero if needed)
 ddd: Abbreviated day name (e.g., Sun for Sunday)
 dddd: Full day name (e.g., Sunday)
 Time:
 h or hh: Hour in 12-hour format (1-12, padded with leading zero if needed)
 H or HH: Hour in 24-hour format (0-23, padded with leading zero if needed)
 m or mm: Minutes (0-59, padded with leading zero if needed)
 s or ss: Seconds (0-59, padded with leading zero if needed)
 a: Meridian indicator (AM or PM)
 */
private val symbolsArgs = listOf(
    "YYYY" to "%1\$z", "yyyy" to "%1\$z",
    "YY" to "%2\$z", "yy" to "%2\$z",
    "Y" to "%3\$z", "y" to "%3\$z",

    "MMMM" to "%4\$z",
    "MMM" to "%5\$z",
    "MM" to "%6\$z",
    "M" to "%7\$z",

    "dddd" to "%8\$z",
    "ddd" to "%9\$z",
    "dd" to "%10\$z",
    "d" to "%11\$z",

    "hh" to "%12\$z",
    "h" to "%13\$z",
    "HH" to "%14\$z",
    "H" to "%15\$z",

    "mm" to "%16\$z",
    "m" to "%17\$z",

    "ss" to "%18\$z",
    "s" to "%19\$z",

    "a" to "%20\$z",
    "A" to "%21\$z",
)

private fun LocalDate.asDateTimeSymbolsArgs(locale: Locale = Locale.getDefault()): Array<String> =
    arrayOf(
        this.year.toString(),
        "%02d".format(this.year % 100),
        "%2d".format(this.year % 100),

        this.month.getDisplayName(TextStyle.FULL, locale),
        this.month.getDisplayName(TextStyle.SHORT, locale),
        "%02d".format(this.monthNumber),
        this.monthNumber.toString(),

        this.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
        this.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
        "%02d".format(this.dayOfMonth),
        this.dayOfMonth.toString(),

        "", "", "", "", // hour
        "", "", // minute
        "", "", // second
        "", "", // am-pm
    )

private fun LocalTime.asDateTimeSymbolsArgs(locale: Locale = Locale.getDefault()): Array<String> =
    arrayOf(
        "", "", "", // year
        "", "", "", "", // month
        "", "", "", "", // day

        "%02d".format((this.hour % 12).run { if (this == 0) 12 else this }),
        "%2d".format((this.hour % 12).run { if (this == 0) 12 else this }),
        "%02d".format(this.hour),
        "%2d".format(this.hour),

        "%02d".format(this.minute),
        "%2d".format(this.minute),

        "%02d".format(this.second),
        "%2d".format(this.second),

        if (this.hour >= 12) "pm" else "am",
        if (this.hour >= 12) "PM" else "AM",
    )

private fun LocalDateTime.asDateTimeSymbolsArgs(
    locale: Locale = Locale.getDefault(),
): Array<String> = arrayOf(
    this.year.toString(),
    "%02d".format(this.year % 100),
    "%2d".format(this.year % 100),

    this.month.getDisplayName(TextStyle.FULL, locale),
    this.month.getDisplayName(TextStyle.SHORT, locale),
    "%02d".format(this.monthNumber),
    this.monthNumber.toString(),

    this.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
    this.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
    "%02d".format(this.dayOfMonth),
    this.dayOfMonth.toString(),

    "%02d".format((this.hour % 12).run { if (this == 0) 12 else this }),
    "%2d".format((this.hour % 12).run { if (this == 0) 12 else this }),
    "%02d".format(this.hour),
    "%2d".format(this.hour),

    "%02d".format(this.minute),
    "%2d".format(this.minute),

    "%02d".format(this.second),
    "%2d".format(this.second),

    if (this.hour >= 12) "pm" else "am",
)

private fun String.mapDateSymbolsToArgs(): DateTimeFormatPattern {
    var newFormat = this
    symbolsArgs.forEach { (symbol, arg) ->
        newFormat = newFormat.replace(symbol, arg)
    }
    return DateTimeFormatPattern(newFormat.replace("\$z", "\$s"))
}

enum class MDDateTimeFormat(val value: String) {

    /**
     * US format: Month/Day/Year (e.g., 06/11/2024)
     */
    US("MM/DD/YYYY"),

    /**
     * European format: Day/Month/Year (e.g., 11/06/2024)
     */
    European("DD/MM/YYYY"),

    /**
     * ISO 8601 format: Year-Month-Day (e.g., 2024-06-11)
     */
    ISO8601("YYYY-MM-DD"),

    /**
     * Full Month Name format: Month Day, Year (e.g., June 11, 2024)
     */
    FullMonthName("MMMM DD, YYYY"),

    /**
     * Abbreviation format: Weekday Abbreviation, Month Abbreviation, Day Year (e.g., Tue, Jun 11 2024)
     */
    Abbreviation("DDD, MMM DD YYYY"),

    /**
     * like [Abbreviation] but without year (e.g., Tue, Jun 11)
     */
    AbbreviationNoYear("DDD, MMM DD"),
    /**
     * like [Abbreviation] but without year and day of week (e.g., Jun 11)
     */
    AbbreviationMonth("MMM DD"),

    /**
     * Dot Separated format: Year.Month.Day (e.g., 2024.06.11)
     */
    DotSeparated("YYYY.MM.DD"),

    /**
     * Time Only (24-hour format): Hour:Minute (e.g., 19:16)
     */
    TimeOnly24("HH:mm"),

    /**
     * Time Only (12-hour format): Hour:Minute (e.g., 7:16)
     */
    TimeOnly12("hh:mm"),

    /**
     * Time with Meridiem Indicator: Hour:Minute AM/PM (e.g., 7:16 PM)
     */
    TimeWithMeridiemIndicator("hh:mm a"),

    /**
     * ISO 8601 with Time format: Year-Month-Day Time (e.g., 2024-06-11T19:16:00)
     */
    ISO8601WithTime("YYYY-MM-DDTHH:mm:ss"),

    /**
     * Year Only format: Year (e.g., 2024)
     */
    YearOnly("YYYY"),

    /**
     * Month Only (Short) format: Abbreviated Month (e.g., Jun)
     */
    MonthOnlyShort("MMM"),

    /**
     * Month Only (Full) format: Full Month Name (e.g., June)
     */
    MonthOnlyFull("MMMM"),

    /**
     * Day of Week Only (Short) format: Abbreviated Weekday (e.g., Tue)
     */
    DayOfWeekOnlyShort("DDD"),

    /**
     * Day of Week Only (Full) format: Full Weekday Name (e.g., Tuesday)
     */
    DayOfWeekOnlyFull("DDDD"),

    /**
     * Day of Month Only format: Numeric Day of the Month (e.g., 11)
     */
    DayOfMonthOnly("DD"),

    /**
     * Day of Year Only format: Numeric Day of the Year (e.g., 162)
     */
    DayOfYearOnly("DDD"),

    /**
     * Week of Year Only format: Week Number (e.g., 23)
     */
    WeekOnly("WW"),

    /**
     * Hour Only format (24-hour): Numeric Hour (e.g., 19)
     */
    HourOnly24("HH"),

    /**
     * Hour Only format (12-hour): Numeric Hour (e.g., 7)
     */
    HourOnly12("hh"),

    /**
     * Minute Only format: Numeric Minute (e.g., 16)
     */
    MinuteOnly("mm"),

    /**
     * Seconds Only format: Numeric Seconds (e.g., 45)
     */
    SecondsOnly("ss");


    val pattern: DateTimeFormatPattern by lazy { value.mapDateSymbolsToArgs() }
}

@JvmInline
value class DateTimeFormatPattern(val value: String)
