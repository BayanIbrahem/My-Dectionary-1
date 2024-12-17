package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class ClosedFloatingPointRangeExtKtTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testSimpleMapping() {
        assertEquals(5f, calculateOutput(0f, 0f, 10f, 5f, 15f), 0.001f)
        assertEquals(10f, calculateOutput(5f, 0f, 10f, 5f, 15f), 0.001f)
        assertEquals(15f, calculateOutput(10f, 0f, 10f, 5f, 15f), 0.001f)
    }

    @Test
    fun testNegativeInputRange() {
        assertEquals(10f, calculateOutput(-10f, -20f, 0f, 5f, 15f), 0.001f)
        assertEquals(10f, calculateOutput(-5f, -10f, 0f, 5f, 15f), 0.001f)
        assertEquals(15f, calculateOutput(0f, -20f, 0f, 5f, 15f), 0.001f)
    }

    @Test
    fun testNegativeOutputRange() {
        assertEquals(-5f, calculateOutput(0f, 0f, 10f, -5f, 5f), 0.001f)
        assertEquals(0f, calculateOutput(5f, 0f, 10f, -5f, 5f), 0.001f)
        assertEquals(5f, calculateOutput(10f, 0f, 10f, -5f, 5f), 0.001f)
    }

    @Test
    fun testReverseMapping() {
        assertEquals(15f, calculateOutput(0f, 10f, 0f, 5f, 15f), 0.001f)
        assertEquals(10f, calculateOutput(5f, 10f, 0f, 5f, 15f), 0.001f)
        assertEquals(5f, calculateOutput(10f, 10f, 0f, 5f, 15f), 0.001f)
    }

    @Test
    fun testSameStartAndEndInput() {
        assertThrows(IllegalArgumentException::class.java) {
            calculateOutput(5f, 5f, 5f, 5f, 15f)
        }
    }

    @Test
    fun testRoundingUp() {
        assertEquals(6f, calculateOutput(0.5f, 0f, 1f, 5f, 7f), 0.001f)
    }
}