package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextTagTest {

    @Test
    fun `contains-sameTag-returnTrue`() {
        val t = ContextTag("object", "food")
        assertTrue(t.contains(t))
    }

    @Test
    fun `contains-differentTags-returnFalse`() {
        val t1 = ContextTag("object", "food")
        val t2 = ContextTag("object", "device")
        assertFalse(t1.contains(t2))
        assertFalse(t2.contains(t1))
    }

    @Test
    fun `contains-subTag-returnTrue`() {
        val t1 = ContextTag("object")
        val t2 = ContextTag("object", "food")
        val t3 = ContextTag("object", "food", "fruit")
        assertTrue(t1.contains(t2))
        assertTrue(t1.contains(t3))
        assertTrue(t2.contains(t3))
        assertFalse(t3.contains(t2))
        assertFalse(t3.contains(t1))
        assertFalse(t2.contains(t1))
    }

    @Test
    fun `isContained-sameTag-returnTrue`() {
        val t = ContextTag("object", "food")
        assertTrue(t.isContained(t))
    }

    @Test
    fun `isContained-differentTags-returnFalse`() {
        val t1 = ContextTag("object", "food")
        val t2 = ContextTag("object", "device")
        assertFalse(t1.isContained(t2))
        assertFalse(t2.isContained(t1))
    }

    @Test
    fun `isContained-subTag-returnTrue`() {
        val t1 = ContextTag("object")
        val t2 = ContextTag("object", "food")
        val t3 = ContextTag("object", "food", "fruit")
        assertFalse(t1.isContained(t2))
        assertFalse(t1.isContained(t3))
        assertFalse(t2.isContained(t3))
        assertTrue(t3.isContained(t2))
        assertTrue(t3.isContained(t1))
        assertTrue(t2.isContained(t1))
    }
}