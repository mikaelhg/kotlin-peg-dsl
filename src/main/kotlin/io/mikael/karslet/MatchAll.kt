package io.mikael.karslet

/**
 * When parsing, every child must match in order
 */
class MatchAll<T> : NonTerminalMatcher<T>() {

    private lateinit var successAction: () -> T

    fun onSuccess(successAction: () -> T) {
        this.successAction = successAction
    }

    fun value() = successAction()

}
