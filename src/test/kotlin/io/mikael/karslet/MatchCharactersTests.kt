package io.mikael.karslet

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.CharBuffer

class MatchCharactersTests {

    @Test
    fun `character consumes exactly one character`() {
        val buffer = CharBuffer.wrap("\"\"x")
        val parser = Karslet.sequence<Unit> {
            character('"')
            onSuccess { }
        }
        Assertions.assertTrue(parser.parse(buffer))
        Assertions.assertEquals(1, buffer.position())
    }

    @Test
    fun `characters stops at max`() {
        val buffer = CharBuffer.wrap("aaaaa")
        val parser = Karslet.sequence<Unit> {
            characters(min = 1, max = 3) { it == 'a' }
            onSuccess { }
        }
        Assertions.assertTrue(parser.parse(buffer))
        Assertions.assertEquals(3, buffer.position())
    }

    @Test
    fun `empty quoted string is parsed`() {
        val parser = Karslet.sequence<String> {
            character('"')
            val body = characters(min = 0) { it != '"' }
            character('"')
            onSuccess { body.value() }
        }
        val buffer = CharBuffer.wrap("\"\",")
        Assertions.assertTrue(parser.parse(buffer))
        Assertions.assertEquals("", parser.value())
        Assertions.assertEquals(2, buffer.position())
    }
}
