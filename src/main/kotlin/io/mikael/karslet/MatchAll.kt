package io.mikael.karslet

import java.io.Reader

/**
 * When parsing, every child must match in order
 */
class MatchAll<T> : NonTerminalMatcher<T>() {

    override fun parse(r: Reader): Boolean {
        resetParserState()
        for (c in children) {
            when (c) {
                is NonTerminalMatcher -> c.beforeAttemptAction()
            }
        }
        r.mark(Integer.MAX_VALUE)
        beforeAttemptAction()
        val success = children.all { it.parse(r) }
        if (!success) {
            r.reset()
            resetParserState()
        }
        return success
    }

}
