package io.mikael.karslet

import java.nio.CharBuffer

/**
 * When parsing, try each child, return the first match.
 * At least one match is required.
 */
open class MatchAny<T> : NonTerminalMatcher<T>() {

    override fun parse(r: CharBuffer): Boolean {
        resetParserState()
        for (c in children) {
            when (c) {
                is NonTerminalMatcher -> c.beforeAttemptAction()
            }
        }
        beforeAttemptAction()
        val startPosition = r.position()
        for (c in children) {
            val success = c.parse(r)
            if (success) {
                return true
            } else {
                resetParserState()
                r.position(startPosition)
            }
        }
        return false
    }

}
