package io.mikael.karslet

import java.io.Reader

class MatchRepeat<T> : NonTerminalMatcher() {

    private lateinit var successAction: () -> T

    private lateinit var iterationAction: () -> Unit

    lateinit var matcher: (Char) -> Boolean

    var exact = -1

    var min = 1

    var max = Integer.MAX_VALUE

    fun match(matcher: (Char) -> Boolean) {
        this.matcher = matcher
    }

    fun onSuccess(successAction: () -> T) {
        this.successAction = successAction
    }

    fun onIteration(iterationAction: () -> Unit) {
        this.iterationAction = iterationAction
    }

    fun value() = successAction()

    override fun parse(r: Reader): Boolean {
        TODO()
    }

}
