package io.mikael.karslet

import java.io.Reader
import java.lang.StringBuilder

@KarsletMarker
class MatchCharacters : TerminalMatcher<String>() {

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

    override fun parse(r: Reader): Boolean {
        var current = 0
        while (true) {
            if (current > max) return true
            r.mark(1)
            val (i, c) = r.readChar()
            when {
                -1 == i -> return current + 1 > min
                matcher(c) -> value.append(c)
                else -> {
                    r.reset()
                    return current + 1 > min
                }
            }
            current += 1
        }
    }

}
