package io.mikael.karslet

import java.io.Reader

/**
 * When parsing, every child must match in order
 */
class MatchAll<T> : NonTerminalMatcher<T>() {

    override fun parse(r: Reader): Boolean {
        r.mark(Integer.MAX_VALUE)
        beforeAttemptAction()
        val success = children.all { it.parse(r) }
        if (!success) r.reset()
        return success
    }

}
