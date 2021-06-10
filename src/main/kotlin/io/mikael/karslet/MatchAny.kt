package io.mikael.karslet

import java.io.Reader

/**
 * When parsing, try each child, return the first match.
 * At least one match is required.
 */
open class MatchAny<T> : NonTerminalMatcher<T>() {

    override fun parse(r: Reader): Boolean {
        resetParserState()
        for (c in children) {
            when (c) {
                is NonTerminalMatcher -> c.beforeAttemptAction()
            }
        }
        r.mark(Integer.MAX_VALUE)
        beforeAttemptAction()
        for (c in children) {
            val success = c.parse(r)
            if (success) {
                return true
            } else {
                resetParserState()
                r.reset()
            }
        }
        return false
    }

}
