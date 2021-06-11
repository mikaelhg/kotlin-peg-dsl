package io.mikael.karslet.operators

import java.nio.CharBuffer

/**
 * When parsing, every child must match in order
 */
class SequenceOperator<T> : NonTerminalOperator<T>() {

    override fun parse(r: CharBuffer): Boolean {
        resetParserState()
        for (c in children) {
            when (c) {
                is NonTerminalOperator -> c.beforeAttemptAction()
            }
        }
        beforeAttemptAction()
        val startPosition = r.position()
        val success = children.all { it.parse(r) }
        if (!success) {
            r.position(startPosition)
            resetParserState()
        }
        return success
    }

}
