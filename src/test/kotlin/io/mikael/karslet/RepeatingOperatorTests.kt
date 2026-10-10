package io.mikael.karslet

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.CharBuffer

class RepeatingOperatorTests {

    private fun letters(min: Int, max: Int = MAX_REPEATS) = Karslet.repeat<List<Char>>(min, max) {
        val seen = mutableListOf<Char>()
        beforeAttempt { seen.clear() }
        val c = characters(min = 1, max = 1) { it.isLetter() }
        onIteration { seen += c.value().single() }
        onSuccess { seen.toList() }
    }

    @Test
    fun `zeroOrMore accepts no matches`() {
        val buffer = CharBuffer.wrap("123")
        val parser = letters(0)
        assertTrue(parser.parse(buffer))
        assertEquals(emptyList<Char>(), parser.value())
        assertEquals(0, buffer.position())
    }

    @Test
    fun `consumes as many iterations as match`() {
        val buffer = CharBuffer.wrap("abc123")
        val parser = letters(0)
        assertTrue(parser.parse(buffer))
        assertEquals(listOf('a', 'b', 'c'), parser.value())
        assertEquals(3, buffer.position())
    }

    @Test
    fun `oneOrMore fails without a match`() {
        assertFalse(letters(1).parse(CharBuffer.wrap("123")))
    }

    @Test
    fun `min is enforced`() {
        assertFalse(letters(3).parse(CharBuffer.wrap("ab1")))
        assertTrue(letters(3).parse(CharBuffer.wrap("abc")))
    }

    @Test
    fun `max caps the iterations`() {
        val buffer = CharBuffer.wrap("abcde")
        val parser = letters(0, 2)
        assertTrue(parser.parse(buffer))
        assertEquals(listOf('a', 'b'), parser.value())
        assertEquals(2, buffer.position())
    }

    @Test
    fun `is possessive and does not give input back`() {
        val parser = Karslet.sequence<Unit> {
            characters(min = 0) { it == 'a' }
            character('a')
            onSuccess { }
        }
        assertFalse(parser.parse(CharBuffer.wrap("aaa")))
    }

    @Test
    fun `optional matches zero or one`() {
        val parser = Karslet.optional<String> {
            val c = character('x')
            onSuccess { c.value() }
        }
        val buffer = CharBuffer.wrap("xx")
        assertTrue(parser.parse(buffer))
        assertEquals(1, buffer.position())
        assertTrue(parser.parse(CharBuffer.wrap("y")))
    }

    @Test
    fun `a failed iteration is rewound`() {
        val parser = Karslet.zeroOrMore<Unit> {
            character('a')
            character('b')
            onSuccess { }
        }
        val buffer = CharBuffer.wrap("ababa")
        assertTrue(parser.parse(buffer))
        assertEquals(4, buffer.position())
    }

    @Test
    fun `failure inside a sequence rewinds past earlier iterations`() {
        val parser = Karslet.sequence<Unit> {
            oneOrMore<Unit> { character('a'); onSuccess { } }
            character('b')
            onSuccess { }
        }
        val buffer = CharBuffer.wrap("aac")
        assertFalse(parser.parse(buffer))
        assertEquals(0, buffer.position())
    }

    @Test
    fun `parse does not invoke onSuccess, value does exactly once per call`() {
        var calls = 0
        val parser = Karslet.zeroOrMore<Int> {
            character('a')
            onSuccess { ++calls }
        }
        assertTrue(parser.parse(CharBuffer.wrap("aaa")))
        assertEquals(0, calls)
        assertEquals(1, parser.value())
        assertEquals(1, calls)
    }

    @Test
    fun `repeat without onSuccess still parses`() {
        val parser = Karslet.zeroOrMore<Unit> { character('a') }
        val buffer = CharBuffer.wrap("aab")
        assertTrue(parser.parse(buffer))
        assertEquals(2, buffer.position())
    }
}
