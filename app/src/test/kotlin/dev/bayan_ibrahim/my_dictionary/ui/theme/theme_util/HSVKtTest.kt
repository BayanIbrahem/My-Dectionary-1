package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HSVKtTest {
    /** hue: 1, saturation: 1, luminance: 1 */
    private lateinit var colorsList1: List<Pair<Color, HsvColor>>

    /** hue: 1, saturation: 0.3, luminance: 1 */
    private lateinit var colorsList2: List<Pair<Color, HsvColor>>

    /** hue: 1, saturation: 0.75, luminance: 1 */
    private lateinit var colorsList3: List<Pair<Color, HsvColor>>

    /** hue: 1, saturation: 1, luminance: 0.3 */
    private lateinit var colorsList4: List<Pair<Color, HsvColor>>
    /** like white and black */
    private lateinit var specialColorsList: List<Pair<Color, HsvColor>>

    @Before
    fun setup() {
        colorsList1 = listOf(
            Color.Red to HsvColor(0f, 1f, 1f),
            Color.Green to HsvColor(0.333f, 1f, 1f),
            Color.Blue to HsvColor(0.667f, 1f, 1f),
            Color.Magenta to HsvColor(0.8333f, 1f, 1f),
            Color.Cyan to HsvColor(0.5f, 1f, 1f),
            Color.Yellow to HsvColor(0.1667f, 1f, 1f),
        )

        colorsList2 = listOf(
            Color(0xFFFFB3B3) to HsvColor(0f, 0.3f, 1f),
            Color(0xFFB3FFB3) to HsvColor(0.333f, 0.3f, 1f),
            Color(0xFFB3B3FF) to HsvColor(0.667f, 0.3f, 1f),
            Color(0xFFFFB3FF) to HsvColor(0.8333f, 0.3f, 1f),
            Color(0xFFB3FFFF) to HsvColor(0.5f, 0.3f, 1f),
            Color(0xFFFFFFB3) to HsvColor(0.1667f, 0.3f, 1f),
        )

        colorsList3 = listOf(
            Color(0xFFFF4040) to HsvColor(0f, 0.75f, 1f),
            Color(0xFF40FF40) to HsvColor(0.333f, 0.75f, 1f),
            Color(0xFF4040FF) to HsvColor(0.667f, 0.75f, 1f),
            Color(0xFFFF40FF) to HsvColor(0.8333f, 0.75f, 1f),
            Color(0xFF40FFFF) to HsvColor(0.5f, 0.75f, 1f),
            Color(0xFFFFFF40) to HsvColor(0.1667f, 0.75f, 1f),
        )

        colorsList4 = listOf(
            Color(0xFF4D0000) to HsvColor(0f, 1f, 0.3f),
            Color(0xFF004D00) to HsvColor(0.333f, 1f, 0.3f),
            Color(0xFF00004D) to HsvColor(0.667f, 1f, 0.3f),
            Color(0xFF4D004D) to HsvColor(0.8333f, 1f, 0.3f),
            Color(0xFF004D4D) to HsvColor(0.5f, 1f, 0.3f),
            Color(0xFF4D4D00) to HsvColor(0.1667f, 1f, 0.3f),
        )
        specialColorsList = listOf(
            Color.White to HsvColor(Float.NaN, 0f, 1f),
            Color.Black to HsvColor(Float.NaN, 0f, 0f),
            Color(0xFF4D4D4D) to HsvColor(Float.NaN, 0f, 0.3f),
            Color(0xFFBFBFBF) to HsvColor(Float.NaN, 0f, 0.75f),
        )
    }

    private fun assertSpecialSimilarColor(required: HsvColor, provided: HsvColor) {
        if (!required.hue.isNaN()) {
            assertEquals(required.hue, provided.hue, 0.005f)
        }
        if (!required.saturation.isNaN()) {
            assertEquals(required.saturation, provided.saturation, 0.005f)
        }

        if (!required.luminance.isNaN()) {
            assertEquals(required.luminance, provided.luminance, 0.005f)
        }
    }

    private fun assertSimilarColor(required: HsvColor, provided: HsvColor) {
        assertEquals(required.hue, provided.hue, 0.005f)
        assertEquals(required.saturation, provided.saturation, 0.005f)
        assertEquals(required.luminance, provided.luminance, 0.005f)
    }

    @Test
    fun `rgbToHsv - specialColors - MatchesResult`() {
        specialColorsList.forEach { (rgb, requiredHsv) ->
            val providedHsv = rgb.hsvColor
            assertSpecialSimilarColor(requiredHsv, providedHsv)
        }
    }

    @Test
    fun `rgbToHsv - group 1 - MatchesResult`() {
        colorsList1.forEach { (rgb, requiredHsv) ->
            val providedHsv = rgb.hsvColor
            assertSimilarColor(requiredHsv, providedHsv)
        }
    }

    @Test
    fun `rgbToHsv - group 2 - MatchesResult`() {
        colorsList2.forEach { (rgb, requiredHsv) ->
            val providedHsv = rgb.hsvColor
            assertSimilarColor(requiredHsv, providedHsv)
        }
    }

    @Test
    fun `rgbToHsv - group 3 - MatchesResult`() {
        colorsList3.forEach { (rgb, requiredHsv) ->
            val providedHsv = rgb.hsvColor
            assertSimilarColor(requiredHsv, providedHsv)
        }
    }

    @Test
    fun `rgbToHsv - group 4 - MatchesResult`() {
        colorsList4.forEach { (rgb, requiredHsv) ->
            val providedHsv = rgb.hsvColor
            assertSimilarColor(requiredHsv, providedHsv)
        }
    }

    private fun assertSpecialSimilarColor(required: Color, provided: Color) {
        if (!required.red.isNaN()) {
            assertEquals(required.red, provided.red, 0.005f)
        }
        if (!required.green.isNaN()) {
            assertEquals(required.green, provided.green, 0.005f)
        }

        if (!required.blue.isNaN()) {
            assertEquals(required.blue, provided.blue, 0.005f)
        }
    }

    private fun assertSimilarColor(required: Color, provided: Color) {
        assertEquals(required.red, provided.red, 0.005f)
        assertEquals(required.green, provided.green, 0.005f)
        assertEquals(required.blue, provided.blue, 0.005f)
    }

    @Test
    fun `hsvToRgb - specialColors - MatchesResult`() {
        specialColorsList.forEach { (requiredRgb, hsv) ->
            val providedRgb = hsv.copy(
                hue = if (hsv.hue.isNaN()) 0f else hsv.hue
            ).rgbColor
            assertSpecialSimilarColor(requiredRgb, providedRgb)
        }
    }

    @Test
    fun `hsvToRgb - group 1 - MatchesResult`() {
        colorsList1.forEach { (requiredRgb, hsv) ->
            val providedRgb = hsv.rgbColor
            assertSimilarColor(requiredRgb, providedRgb)
        }
    }

    @Test
    fun `hsvToRgb - group 2 - MatchesResult`() {
        colorsList2.forEach { (requiredRgb, hsv) ->
            val providedRgb = hsv.rgbColor
            assertSimilarColor(requiredRgb, providedRgb)
        }
    }

    @Test
    fun `hsvToRgb - group 3 - MatchesResult`() {
        colorsList3.forEach { (requiredRgb, hsv) ->
            val providedRgb = hsv.rgbColor
            assertSimilarColor(requiredRgb, providedRgb)
        }
    }

    @Test
    fun `hsvToRgb - group 4 - MatchesResult`() {
        colorsList4.forEach { (requiredRgb, hsv) ->
            val providedRgb = hsv.rgbColor
            assertSimilarColor(requiredRgb, providedRgb)
        }
    }
}