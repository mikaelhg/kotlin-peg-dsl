package io.mikael.karslet

object Karslet {

    fun <T> parser(init: MatchAll.() -> Unit): MatchAll {
        val rule = MatchAll()
        rule.init()
        return rule
    }

    fun <T> parser(context: T, init: MatchAll.() -> Unit): MatchAll {
        val rule = MatchAll()
        rule.init()
        return rule
    }

}