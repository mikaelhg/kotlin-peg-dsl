package io.mikael.karslet

object Karslet {

    fun <T> parser(init: MatchAll<T>.() -> Unit) = MatchAll<T>().also(init)

}
