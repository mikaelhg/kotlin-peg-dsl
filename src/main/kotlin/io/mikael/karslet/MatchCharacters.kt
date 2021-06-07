package io.mikael.karslet

import java.io.Reader
import java.lang.StringBuilder

@KarsletMarker
class MatchCharacters : TerminalMatcher() {

    private lateinit var matcher: (Char) -> Boolean

    private val sb = StringBuilder()

    var min = 1

    var max = Integer.MAX_VALUE

    fun match(matcher: (Char) -> Boolean) {
        this.matcher = matcher
    }

    fun value() = sb.toString()

    override fun parse(r: Reader): Boolean {
        var current = 0
        while (true) {
            if (current > max) return true
            r.mark(1)
            val (i, c) = r.readChar()
            when {
                -1 == i -> return current + 1 > min
                matcher(c) -> sb.append(c)
                else -> {
                    r.reset()
                    return current + 1 > min
                }
            }
            current += 1
        }
    }

}
