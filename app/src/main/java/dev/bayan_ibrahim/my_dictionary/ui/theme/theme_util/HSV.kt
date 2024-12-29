package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.roundToInt

private fun Int.posMod(other: Int) = mod(other).plus(other).mod(other)
class MutableHsvColor(
    hue: Float = 0f,
    saturation: Float = 1f,
    luminance: Float = 1f,
) : HsvColor {
    constructor(hsvColor: HsvColor) : this(hsvColor.hue, hsvColor.saturation, hsvColor.luminance)
    constructor(color: Color) : this(color.hsvColor)

    override var hue: Float by mutableFloatStateOf(hue)
    override var saturation: Float by mutableFloatStateOf(saturation)
    override var luminance: Float by mutableFloatStateOf(luminance)
    fun setFrom(hsvColor: HsvColor) {
        hue = hsvColor.hue
        saturation = hsvColor.saturation
        luminance = hsvColor.luminance
    }

    fun setFrom(color: Color) = setFrom(color.hsvColor)
}

interface HsvColor {
    val hue: Float
    val saturation: Float
    val luminance: Float

    companion object {
        operator fun invoke(
            hue: Float = 0f,
            saturation: Float = 1f,
            luminance: Float = 1f,
        ): HsvColor = MutableHsvColor(hue, saturation, luminance)
    }

    fun copy(
        hue: Float = this.hue,
        saturation: Float = this.saturation,
        luminance: Float = this.luminance,
    ): HsvColor = HsvColor(hue, saturation, luminance)

    /**
     * @degree  from 0 to 359 (if larger or smaller than this range we its module)
     */
    fun hue(degree: Int): HsvColor = copy(hue = degree.posMod(360) / 360f)

    /**
     * @param percent from 0 to 100 passed value would be coerced to [0..100]
     */
    fun saturation(percent: Int): HsvColor = copy(saturation = percent.coerceIn(0, 100) / 100f)

    /**
     * @param percent from 0 to 100 passed value would be coerced to [0..100]
     */
    fun luminance(percent: Int): HsvColor = copy(luminance = percent.coerceIn(0, 100) / 100f)
}

/**
 * @param value from 0 to 255
 */
fun Color.red(value: Int): Color = copy(red = value.coerceIn(0, 255) / 255f)

/**
 * @param value from 0 to 255
 */
fun Color.green(value: Int): Color = copy(green = value.coerceIn(0, 255) / 255f)

/**
 * @param value from 0 to 255
 */
fun Color.blue(value: Int): Color = copy(blue = value.coerceIn(0, 255) / 255f)

val Color.hsvColor: HsvColor
    get() {
        val max = listOf(red, green, blue).max()
        val min = listOf(red, green, blue).min()
        val delta = max - min

        val luminance = max

        val saturation = if (delta == 0f) 0f else delta / max

        val hue = if (delta == 0f) {
            0f
        } else {
            when (max) {
                red -> (green - blue) / delta
                green -> 2 + (blue - red) / delta
                else -> 4 + (red - green) / delta
            }.div(6).plus(1f).mod(1f)
        }

        return HsvColor(hue = hue, saturation = saturation, luminance = luminance)
    }

val HsvColor.rgbColor: Color
    get() {
        val degreeHue = (360 * hue).roundToInt()
        val chroma = luminance * saturation
        val intermediate = chroma * (1 - abs(degreeHue.div(60f).mod(2f).minus(1)))
        val matchValue = luminance - chroma
        val (r, g, b) = when (degreeHue.mod(360)) {
            in 0..60 -> Triple(chroma, intermediate, 0f)
            in 60..120 -> Triple(intermediate, chroma, 0f)
            in 120..180 -> Triple(0f, chroma, intermediate)
            in 180..240 -> Triple(0f, intermediate, chroma)
            in 240..300 -> Triple(intermediate, 0f, chroma)
            in 300..360 -> Triple(chroma, 0f, intermediate)
            else -> throw IllegalArgumentException("Hue value must be in the range [0, 360]")
        }
        return Color(
            red = r.plus(matchValue).let { if (it > 1f) it.mod(1f) else it },
            green = g.plus(matchValue).let { if (it > 1f) it.mod(1f) else it },
            blue = b.plus(matchValue.let { if (it > 1f) it.mod(1f) else it })
        )
    }
