package io.mikael.karslet

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.CharBuffer

class OrderedChoiceOperatorTests {

    private fun choice(): io.mikael.karslet.operators.OrderedChoiceOperator<String> {
        lateinit var first: io.mikael.karslet.Parser<String>
        lateinit var second: io.mikael.karslet.Parser<String>
        return Karslet.choice {
            first = sequence<String> {
                val a = character('a')
                val b = character('b')
                onSuccess { a.value() + b.value() }
            }
            second = sequence<String> {
                val a = character('a')
                val c = character('c')
                onSuccess { a.value() + c.value() }
            }
            onSuccess { "first=" + first.value() + ",second=" + second.value() }
        }
    }

    @Test
    fun `takes the first matching alternative`() {
        val buffer = CharBuffer.wrap("abz")
        val parser = choice()
        assertTrue(parser.parse(buffer))
        assertEquals(2, buffer.position())
    }

    @Test
    fun `backtracks to a later alternative after a partial match`() {
        val buffer = CharBuffer.wrap("acz")
        val parser = choice()
        assertTrue(parser.parse(buffer))
        assertEquals(2, buffer.position())
        assertEquals("first=,second=ac", parser.value())
    }

    @Test
    fun `is ordered, not longest match`() {
        val parser = Karslet.choice<Unit> {
            characters(min = 1, max = 1) { it == 'a' }
            characters(min = 1, max = 2) { it == 'a' }
            onSuccess { }
        }
        val buffer = CharBuffer.wrap("aa")
        assertTrue(parser.parse(buffer))
        assertEquals(1, buffer.position())
    }

    @Test
    fun `fails and rewinds when no alternative matches`() {
        val buffer = CharBuffer.wrap("xyz")
        assertFalse(choice().parse(buffer))
        assertEquals(0, buffer.position())
    }
}
