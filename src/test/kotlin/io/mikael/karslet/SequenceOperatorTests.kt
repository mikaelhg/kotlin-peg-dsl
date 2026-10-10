package io.mikael.karslet

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.CharBuffer

class SequenceOperatorTests {

    private fun abc() = Karslet.sequence<String> {
        val a = character('a')
        val b = character('b')
        val c = character('c')
        onSuccess { a.value() + b.value() + c.value() }
    }

    @Test
    fun `matches all children in order`() {
        val buffer = CharBuffer.wrap("abcd")
        val parser = abc()
        assertTrue(parser.parse(buffer))
        assertEquals("abc", parser.value())
        assertEquals(3, buffer.position())
    }

    @Test
    fun `fails and rewinds when a child fails`() {
        val buffer = CharBuffer.wrap("abx")
        assertFalse(abc().parse(buffer))
        assertEquals(0, buffer.position())
    }

    @Test
    fun `fails on premature end of input`() {
        val buffer = CharBuffer.wrap("ab")
        assertFalse(abc().parse(buffer))
        assertEquals(0, buffer.position())
    }

    @Test
    fun `is reusable after a failure`() {
        val parser = abc()
        assertFalse(parser.parse(CharBuffer.wrap("abx")))
        assertTrue(parser.parse(CharBuffer.wrap("abc")))
        assertEquals("abc", parser.value())
    }

    @Test
    fun `nested sequences compose`() {
        val parser = Karslet.sequence<String> {
            val inner = include(abc())
            val d = character('d')
            onSuccess { inner.value() + d.value() }
        }
        assertTrue(parser.parse(CharBuffer.wrap("abcd")))
        assertEquals("abcd", parser.value())
    }
}
