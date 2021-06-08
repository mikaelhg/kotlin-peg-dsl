package io.mikael.karslet

import java.io.Reader

class MatchRepeat<T> : NonTerminalMatcher<T>() {

    private lateinit var successAction: () -> T

    private lateinit var iterationAction: () -> Unit

    var min = 1

    var max = Integer.MAX_VALUE

    /**
     * Every time we've successfully played all of our children, we call this.
     */
    fun onIteration(iterationAction: () -> Unit) {
        this.iterationAction = iterationAction
    }

    /**
     * When we're done repeating, we'll call this.
     */
    fun onSuccess(successAction: () -> T) {
        this.successAction = successAction
    }

    fun value() = successAction()

    private fun loopThroughChildren(r: Reader): Boolean {
        r.mark(Integer.MAX_VALUE)
        val success = children.all { it.parse(r) }
        if (!success) {
            r.reset()
        }
        return success
    }

    override fun parse(r: Reader): Boolean {
        var current = 1
        while (true) {
            if (current > max) break
            val iterationSuccess = loopThroughChildren(r)
            if (!iterationSuccess) break
            iterationAction()
            this.resetParserState()
            current += 1
        }
        val success = current in min until max
        if (success) successAction()
        return success
    }

}
