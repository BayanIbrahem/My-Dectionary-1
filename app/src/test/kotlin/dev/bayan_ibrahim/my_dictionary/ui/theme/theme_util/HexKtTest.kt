package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import org.junit.Assert.assertEquals
import org.junit.Test
import androidx.compose.ui.graphics.Color

class HexKtTest {

    @Test
    fun `test valid 8-character hex`() {
        val hex = "FFAABBCC"
        val expected = Color(
            red = 0xAA,
            green = 0xBB,
            blue = 0xCC,
            alpha = 0xFF,
        )
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test valid 6-character hex`() {
        val hex = "AABBCC"
        val expected = Color(
            red = 0xAA,
            green = 0xBB,
            blue = 0xCC,
        )
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test valid short hex with padding`() {
        val hex = "ABC"
        // Padded to "FF000ABC"
        val expected = Color(
            red = 0x00,
            green = 0x0A,
            blue = 0xBC,
        )
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test single character hex`() {
        val hex = "F"
        // Padded to FF00000F
        val expected = Color(
            red = 0x00,
            green = 0x00,
            blue = 0x0F,
        )
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test invalid hex length greater than 8`() {
        val hex = "123456789"
        val expected = Color(
            red = 0x34,
            green = 0x56,
            blue = 0x78,
            alpha = 0x12
        )
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test invalid hex with non-hex characters`() {
        val hex = "ZZZ123"
        // Filtered To 123 then padded to FF000123
        val expected = Color(
            red = 0x00,
            green = 0x01,
            blue = 0x23,
            alpha = 0xFF
        ) // Sanitized to "FF000001"
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test empty string`() {
        val hex = ""
        // padded to FF000000
        val expected = Color(
            red = 0x00,
            green = 0x00,
            blue = 0x00,
            alpha = 0xFF
        ) // Defaults to transparent
        assertEquals(expected, safeHexToColor(hex))
    }

    @Test
    fun `test hex with mixed case characters`() {
        val hex = "aAbBcCdD"
        val expected = Color(
            red = 0xBB,
            green = 0xCC,
            blue = 0xDD,
            alpha = 0xAA,
        ) // Handles case-insensitivity
        assertEquals(expected, safeHexToColor(hex))
    }
}
