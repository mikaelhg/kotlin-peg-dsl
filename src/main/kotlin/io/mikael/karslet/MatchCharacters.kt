package io.mikael.karslet

import java.nio.BufferUnderflowException
import java.nio.CharBuffer

@KarsletMarker
open class MatchCharacters : TerminalMatcher<String>() {

    private lateinit var matcher: (Char) -> Boolean

    private val value = StringBuilder()

    @ParserConfiguration
    var min = 1

    @ParserConfiguration
    var max = Integer.MAX_VALUE

    @ParserConfiguration
    fun match(matcher: (Char) -> Boolean) {
        this.matcher = matcher
    }

    override fun value() = value.toString()

    override fun resetParserState() {
        value.clear()
    }

    override fun parse(r: CharBuffer): Boolean {
        var current = 0
        while (true) {
            if (current > max) return true
            val startPosition = r.position()
            try {
                val c = r.get()
                if (matcher(c)) {
                    value.append(c)
                } else {
                    r.position(startPosition)
                    return current + 1 > min
                }
            } catch (e: BufferUnderflowException) {
                return current + 1 > min
            }
            current += 1
        }
    }

}
