package io.mikael.karslet.operators

import io.mikael.karslet.MAX_REPEATS
import io.mikael.karslet.ParserConfiguration
import java.nio.CharBuffer

open class RepeatingOperator<T>() : NonTerminalOperator<T>() {

    private var iterationAction: () -> Unit = {}

    @ParserConfiguration
    var min = 1

    @ParserConfiguration
    var max = MAX_REPEATS

    constructor(min: Int = 0, max: Int = MAX_REPEATS, iterationAction: () -> Unit = {}) : this() {
        this.min = min
        this.max = max
        this.iterationAction = iterationAction
    }

    /**
     * Every time we've successfully played all of our children, we call this.
     */
    @ParserConfiguration
    fun onIteration(iterationAction: () -> Unit) {
        this.iterationAction = iterationAction
    }

    protected open fun loopThroughChildren(r: CharBuffer): Boolean {
        val startPosition = r.position()
        val success = children.all { it.parse(r) }
        if (!success) {
            r.position(startPosition)
        }
        return success
    }

    override fun parse(r: CharBuffer): Boolean {
        resetParserState()
        for (c in children) {
            when (c) {
                is NonTerminalOperator -> c.beforeAttemptAction()
            }
        }
        beforeAttemptAction()
        var current = 0
        while (true) {
            resetParserState()
            if (current >= max) break
            val iterationSuccess = loopThroughChildren(r)
            if (iterationSuccess) {
                iterationAction()
            } else {
                break
            }
            current += 1
        }
        val success = current in min .. max
        if (success) successAction()
        resetParserState()
        return success
    }

}
