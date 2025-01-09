package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import kotlin.math.roundToInt

private val hexNumberChars = "0123456789abcdefABCDEF".toSet()
fun Color.Companion.strHex(hex: String): Color = safeHexToColor(hex)

fun String.toColor(): Color = safeHexToColor(this)

fun safeHexToColor(hex: String): Color {
    val sanitizedHex = hex.filter { it in hexNumberChars }
    val fullHex = when (sanitizedHex.length) {
        8 -> sanitizedHex // Already includes alpha
        7 -> "F$sanitizedHex" // Add alpha = 255
        6 -> "FF$sanitizedHex" // Add alpha = 255
        in 0..5 -> sanitizedHex.padStart(6, '0').let { "FF$it" } // Pad with zeros and add alpha
        else -> sanitizedHex.subSequence(0, 8)
    }
    val a = fullHex.subSequence(0, 2).toString().toInt(16)
    val r = fullHex.subSequence(2, 4).toString().toInt(16)
    val g = fullHex.subSequence(4, 6).toString().toInt(16)
    val b = fullHex.subSequence(6, 8).toString().toInt(16)

    return Color(red = r, green = g, blue = b, alpha = a)
}

fun Color.toStrHex(): String = buildString {
    append("#")
    append(alpha.to8BitHexString())
    append(red.to8BitHexString())
    append(green.to8BitHexString())
    append(blue.to8BitHexString())
}

/**
 * return a value from 0 to 255 but in hex (00 to FF)
 */
@OptIn(ExperimentalStdlibApi::class)
fun Float.to8BitHexString(
    inputRange: ClosedFloatingPointRange<Float> = 0f..1f,
): String {
    return calculateOutput(
        input = this,
        inputRangeStart = inputRange.start,
        inputRangeEnd = inputRange.endInclusive,
        outputRangeStart = 0f,
        outputRangeEnd = 255f
    ).roundToInt()
        .coerceIn(0, 255)
        .toHexString(HexFormat.UpperCase).padStart(2, '0')
}