package io.mikael.karslet

object Karslet {

    fun <T> parser(init: MatchAll<T>.() -> Unit): MatchAll<T> {
        val rule = MatchAll<T>()
        rule.init()
        return rule
    }

    fun <T> parser(context: T, init: MatchAll<T>.() -> Unit): MatchAll<T> {
        val rule = MatchAll<T>()
        rule.init()
        return rule
    }

}
